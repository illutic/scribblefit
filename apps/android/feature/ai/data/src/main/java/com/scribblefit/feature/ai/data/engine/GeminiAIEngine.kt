package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta"

class GeminiAIEngine(
    private val httpClient: HttpClient,
    private val secureKeyStorage: SecureKeyStorage,
    private val json: Json,
    private val prompt: String
) : LLMEngine, AnalysisEngine {

    override suspend fun parseWorkout(rawText: String): ParsedWorkoutResult {
        val startMs = System.currentTimeMillis()
        return try {
            val apiKey = secureKeyStorage.getApiKey() ?: return ParsedWorkoutResult(
                workout = null, rawText = rawText, status = ParsingStatus.FAILURE, error = "No API key"
            )
            val responseText = callGemini(apiKey, "$prompt\n\nInput: $rawText")
            val workout = json.decodeFromString<ParsedWorkout>(responseText)
            ParsedWorkoutResult(
                workout = workout,
                rawText = rawText,
                status = ParsingStatus.SUCCESS,
                processingTimeMs = System.currentTimeMillis() - startMs
            )
        } catch (e: Exception) {
            ParsedWorkoutResult(
                workout = null, rawText = rawText, status = ParsingStatus.FAILURE,
                error = e.message, processingTimeMs = System.currentTimeMillis() - startMs
            )
        }
    }

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> =
        Result.failure(NotImplementedError("Gemini analysis not yet implemented"))

    override suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary> =
        Result.failure(NotImplementedError("Gemini analysis not yet implemented"))

    override suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight> =
        Result.failure(NotImplementedError("Gemini analysis not yet implemented"))

    private suspend fun callGemini(apiKey: String, userPrompt: String): String {
        @Serializable
        data class Part(val text: String)
        @Serializable
        data class Content(val parts: List<Part>)
        @Serializable
        data class GenerationConfig(val responseMimeType: String)
        @Serializable
        data class GeminiRequest(val contents: List<Content>, val generationConfig: GenerationConfig)
        @Serializable
        data class Candidate(val content: Content)
        @Serializable
        data class GeminiResponse(val candidates: List<Candidate>)

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(userPrompt)))),
            generationConfig = GenerationConfig(responseMimeType = "application/json")
        )
        val response = httpClient.post("$GEMINI_BASE_URL/models/gemini-1.5-flash:generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val geminiResponse = response.body<GeminiResponse>()
        return geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
    }
}
