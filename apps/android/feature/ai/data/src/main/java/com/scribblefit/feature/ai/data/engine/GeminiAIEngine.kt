package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.feature.ai.data.entity.WorkoutDto
import com.scribblefit.feature.ai.data.entity.toDomain
import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.model.ParsingStatus
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta"

internal class GeminiAIEngine(
    private val httpClient: HttpClient,
    private val secureKeyStorage: SecureKeyStorage,
    private val configRepository: ConfigRepository,
    private val json: Json
) : LLMEngine, AnalysisEngine {
    private val config get() = configRepository.config.value
    private val apiKey get() = secureKeyStorage.getApiKey() ?: error("API Key is not provided")

    @InternalSerializationApi
    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult> = runCatching {
        val prompt = config.parsePrompt
        val startMs = System.currentTimeMillis()
        val responseText = callGemini(apiKey, "$prompt\n\nInput: $rawText")
        val workout = json.decodeFromString<WorkoutDto>(responseText)

        ParsedWorkoutResult(
            workout = workout.toDomain(),
            rawText = rawText,
            status = ParsingStatus.SUCCESS,
            processingTimeMs = System.currentTimeMillis() - startMs
        )
    }

    @InternalSerializationApi
    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> =
        runCatching {
            val responseText =
                callGemini(apiKey, "${config.suggestionPrompt}\n\nContext:\n$context")
            val dto = json.decodeFromString<SuggestionResponseDto>(responseText)
            dto.toDomain()
        }

    @InternalSerializationApi
    override suspend fun generateSummary(
        period: SummaryPeriod,
        workoutData: String
    ): Result<AnalysisSummary> = runCatching {
        val responseText =
            callGemini(
                apiKey,
                "${config.summaryPrompt}\n\nPeriod: ${period.name}\nData:\n$workoutData"
            )
        val dto = json.decodeFromString<SummaryResponseDto>(responseText)

        dto.toDomain(period)
    }

    @InternalSerializationApi
    override suspend fun generateExerciseInsight(
        exerciseId: String
    ): Result<ExerciseInsight> = runCatching {
        // TODO - get exercise data and history based on exerciseId, for now using placeholders
        val exerciseName = "Squat"
        val historyData = "Week 1: 3 sets of 5 reps at 100kg\nWeek 2: 3 sets of 5 reps at 105kg\n"

        val responseText =
            callGemini(
                apiKey,
                "${config.insightPrompt}\n\nExercise: $exerciseName\nHistory:\n$historyData"
            )
        val dto = json.decodeFromString<InsightResponseDto>(responseText)

        dto.toDomain(exerciseId)
    }

    @InternalSerializationApi
    private suspend fun callGemini(apiKey: String, userPrompt: String): String {
        @Serializable
        data class Part(val text: String)

        @Serializable
        data class Content(val parts: List<Part>)

        @Serializable
        data class GenerationConfig(val responseMimeType: String)

        @Serializable
        data class GeminiRequest(
            val contents: List<Content>,
            val generationConfig: GenerationConfig
        )

        @Serializable
        data class Candidate(val content: Content)

        @Serializable
        data class GeminiResponse(val candidates: List<Candidate>)

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(userPrompt)))),
            generationConfig = GenerationConfig(responseMimeType = "")
        )
        val model = config.preferredModel ?: error("No model selected")
        val response =
            httpClient.post("$GEMINI_BASE_URL/models/$model:generateContent?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        val geminiResponse = response.body<GeminiResponse>()
        val resultText =
            geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
        return resultText.replaceFirst("```json", "").replaceFirst("```", "").trim()
    }
}
