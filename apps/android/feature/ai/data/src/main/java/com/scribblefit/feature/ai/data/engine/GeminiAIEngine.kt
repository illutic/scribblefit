package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.data.mapper.*
import com.scribblefit.core.ai.engine.*
import com.scribblefit.core.ai.model.*
import com.scribblefit.core.network.model.ParsedWorkoutDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Named

class GeminiAIEngine @Inject constructor(
    @param:Named("base") private val client: HttpClient,
    private val apiKey: String,
    private val systemPrompt: String,
    private val json: Json
) : LLMEngine, AnalysisEngine {

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:"
    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> = runCatching {
        val content = callGemini(systemPrompt, rawText)
        try {
            val parsedWorkoutDto = json.decodeFromString<ParsedWorkoutDto>(content)
            parsedWorkoutDto.toDomain()
        } catch (e: Exception) {
            throw AIParsingException(rawText = rawText, error = "Hallucination: ${e.message}", cause = e)
        }
    }

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> = runCatching {
        val content = callGemini(AnalysisPrompts.getSuggestionPrompt(context), "Generate suggestion.")
        json.decodeFromString<SuggestionDto>(content).toDomain()
    }

    override suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary> = runCatching {
        val content = callGemini(AnalysisPrompts.getSummaryPrompt(period.name, workoutData), "Generate summary.")
        json.decodeFromString<SummaryDto>(content).toDomain(period)
    }

    override suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight> = runCatching {
        val content = callGemini(AnalysisPrompts.getExerciseInsightPrompt(exerciseName, historyData), "Analyze $exerciseName.")
        json.decodeFromString<ExerciseInsightDto>(content).toDomain(exerciseName)
    }

    private suspend fun callGemini(prompt: String, userMessage: String): String {
        val response = client.post("${baseUrl}generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(
                GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = userMessage))
                        )
                    ),
                    systemInstruction = GeminiSystemInstruction(
                        parts = listOf(GeminiPart(text = prompt))
                    ),
                    generationConfig = GeminiGenerationConfig(
                        responseMimeType = "application/json"
                    )
                )
            )
        }.body<GeminiResponse>()

        return response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("Empty response from Gemini")
    }
}

@Serializable
private data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiSystemInstruction,
    val generationConfig: GeminiGenerationConfig
)

@Serializable
private data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
private data class GeminiSystemInstruction(
    val parts: List<GeminiPart>
)

@Serializable
private data class GeminiPart(
    val text: String
)

@Serializable
private data class GeminiGenerationConfig(
    val responseMimeType: String
)

@Serializable
private data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

@Serializable
private data class GeminiCandidate(
    val content: GeminiContent
)
