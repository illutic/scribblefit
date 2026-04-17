package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SecureKeyStorage
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.ai.data.entity.AIInsightDto
import com.scribblefit.feature.ai.data.entity.WorkoutDto
import com.scribblefit.feature.ai.data.entity.toDomain
import com.scribblefit.feature.ai.domain.CloudLLMEngine
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.ParsingStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta"

internal class GeminiAIEngine(
    private val httpClient: HttpClient,
    private val secureKeyStorage: SecureKeyStorage,
    private val configRepository: ConfigRepository,
    private val json: Json
) : LLMEngine, CloudLLMEngine {
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
            parsedJson = responseText,
            processingTimeMs = System.currentTimeMillis() - startMs
        )
    }

    override suspend fun generateInsightsSummary(exercises: List<Exercise>): Result<List<AIInsight>> =
        runCatching {
            val context = exercises.joinToString("\n") { exercise ->
                val sets = exercise.sets.joinToString(", ") { "${it.weight}x${it.reps}" }
                "${exercise.canonicalName} (${exercise.muscleGroup}): $sets"
            }

            val prompt = "${config.summaryPrompt}\n\nData:\n$context"

            val responseText = callGemini(apiKey, prompt)
            val dtos = json.decodeFromString<List<AIInsightDto>>(responseText)
            dtos.map { it.toDomain() }
        }

    override suspend fun validateApiKey(apiKey: String): Result<Unit> = runCatching {
        val response = httpClient.get("$GEMINI_BASE_URL/models?key=$apiKey")
        if (response.status != HttpStatusCode.OK) {
            error("Invalid API Key: ${response.status}")
        }
    }

    override suspend fun getAvailableModels(apiKey: String): Result<List<String>> = runCatching {
        val response = httpClient.get("$GEMINI_BASE_URL/models?key=$apiKey")
        if (response.status != HttpStatusCode.OK) {
            error("Failed to fetch models: ${response.status}")
        }
        val modelList = response.body<ModelListResponse>()
        modelList.models
            .filter { it.supportedGenerationMethods.contains("generateContent") }
            .map { it.name.removePrefix("models/") }
    }

    private suspend fun callGemini(apiKey: String, userPrompt: String): String {
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

@Serializable
private data class ModelDto(
    val name: String,
    val supportedGenerationMethods: List<String>
)

@Serializable
private data class ModelListResponse(
    val models: List<ModelDto>
)

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
