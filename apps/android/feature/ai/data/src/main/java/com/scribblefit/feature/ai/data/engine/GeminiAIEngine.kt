package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SecureKeyStorage
import com.scribblefit.feature.ai.data.entity.WorkoutDto
import com.scribblefit.feature.ai.data.entity.toDomain
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.ParsingStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta"

internal class GeminiAIEngine(
    private val httpClient: HttpClient,
    private val secureKeyStorage: SecureKeyStorage,
    private val configRepository: ConfigRepository,
    private val json: Json
) : LLMEngine {
    private val config get() = configRepository.config.value
    private val apiKey get() = secureKeyStorage.getApiKey() ?: error("API Key is not provided")

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
