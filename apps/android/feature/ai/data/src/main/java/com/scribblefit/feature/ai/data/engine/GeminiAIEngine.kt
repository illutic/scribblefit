package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.network.model.ParsedWorkoutDto
import com.scribblefit.feature.ai.data.mapper.ExerciseInsightDto
import com.scribblefit.feature.ai.data.mapper.SuggestionDto
import com.scribblefit.feature.ai.data.mapper.SummaryDto
import com.scribblefit.feature.ai.data.mapper.toDomain
import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.ConfigRepository
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
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Named

class GeminiAIEngine @Inject constructor(
    @param:Named("base") private val client: HttpClient,
    private val secureKeyStorage: SecureKeyStorage,
    private val configRepository: ConfigRepository,
    private val json: Json
) : LLMEngine, AnalysisEngine {

    private val apiBase = "https://generativelanguage.googleapis.com/v1beta"
    private val logger = LoggerFactory.getLogger(javaClass)

    private var activeModelPath: String? = null
    private val modelMutex = Mutex()

    override suspend fun parseWorkout(rawText: String): ParsedWorkoutResult {
        val startTime = System.currentTimeMillis()
        return try {
            val apiKey = secureKeyStorage.getApiKey() ?: ""
            val systemPrompt = configRepository.getConfig().first()?.promptText
                ?: error("Prompt is empty. Configuration is not set.")

            val content = callGemini(apiKey, systemPrompt, "$rawText\n\nOutput in JSON format.")
            val duration = System.currentTimeMillis() - startTime

            val parsedWorkoutDto = json.decodeFromString<ParsedWorkoutDto>(content)
            val workout = parsedWorkoutDto.toDomain()

            ParsedWorkoutResult(
                workout = workout,
                rawText = rawText,
                status = ParsingStatus.SUCCESS,
                modelUsed = activeModelPath ?: "gemini-flash",
                processingTimeMs = duration
            )
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            ParsedWorkoutResult(
                workout = null,
                rawText = rawText,
                status = ParsingStatus.FAILURE,
                modelUsed = activeModelPath ?: "gemini-flash",
                processingTimeMs = duration,
                error = e.message ?: "Unknown error"
            )
        }
    }

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> =
        runCatching {
            val apiKey = secureKeyStorage.getApiKey() ?: ""
            val content = callGemini(
                apiKey,
                AnalysisPrompts.getSuggestionPrompt(context),
                "Generate suggestion in JSON format."
            )
            json.decodeFromString<SuggestionDto>(content).toDomain()
        }

    override suspend fun generateSummary(
        period: SummaryPeriod,
        workoutData: String
    ): Result<AnalysisSummary> = runCatching {
        val apiKey = secureKeyStorage.getApiKey() ?: ""
        val content = callGemini(
            apiKey,
            AnalysisPrompts.getSummaryPrompt(period.name, workoutData),
            "Generate summary in JSON format."
        )
        json.decodeFromString<SummaryDto>(content).toDomain(period)
    }

    override suspend fun generateExerciseInsight(
        exerciseName: String,
        historyData: String
    ): Result<ExerciseInsight> = runCatching {
        val apiKey = secureKeyStorage.getApiKey() ?: ""
        val content = callGemini(
            apiKey,
            AnalysisPrompts.getExerciseInsightPrompt(exerciseName, historyData),
            "Analyze $exerciseName in JSON format."
        )
        json.decodeFromString<ExerciseInsightDto>(content).toDomain(exerciseName)
    }

    private suspend fun getOrDiscoverModel(apiKey: String): String {
        // If user has explicitly selected a model, use it
        val configuredModel = configRepository.getConfig().first()?.preferredModel
        if (!configuredModel.isNullOrEmpty()) {
            activeModelPath = configuredModel
            return configuredModel
        }

        modelMutex.withLock {
            if (activeModelPath != null) return activeModelPath!!

            logger.info("Discovering available Gemini models...")
            try {
                val response =
                    client.get("$apiBase/models?key=$apiKey").body<GeminiModelListResponse>()
                // Find a model that supports generateContent and is a "flash" or "pro" model
                val model = response.models
                    .filter { it.supportedGenerationMethods.contains("generateContent") }
                    .filter { it.name.contains("flash", ignoreCase = true) }
                    .maxByOrNull { it.name }

                activeModelPath = model?.name ?: "models/gemini-1.5-flash"
                logger.info("Selected model: $activeModelPath")
                return activeModelPath!!
            } catch (e: Exception) {
                logger.error("Failed to list models, falling back to default", e)
                return "models/gemini-1.5-flash"
            }
        }
    }

    private suspend fun callGemini(apiKey: String, prompt: String, userMessage: String): String {
        val modelPath = getOrDiscoverModel(apiKey)
        val fullUserMessage = "$prompt\n\n$userMessage"

        val response = client.post("$apiBase/$modelPath:generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(
                GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            role = "user",
                            parts = listOf(GeminiPart(text = fullUserMessage))
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        responseMimeType = "application/json"
                    )
                )
            )
        }.body<GeminiResponse>()

        return response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("Empty response from Gemini ($activeModelPath)")
    }
}

@Serializable
private data class GeminiModelListResponse(
    val models: List<GeminiModelInfo>
)

@Serializable
private data class GeminiModelInfo(
    val name: String,
    val version: String? = null,
    val displayName: String? = null,
    val description: String? = null,
    val inputTokenLimit: Int? = null,
    val outputTokenLimit: Int? = null,
    val supportedGenerationMethods: List<String> = emptyList()
)

@Serializable
private data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerialName("generationConfig") val generationConfig: GeminiGenerationConfig
)

@Serializable
private data class GeminiContent(
    val role: String,
    val parts: List<GeminiPart>
)

@Serializable
private data class GeminiPart(
    val text: String
)

@Serializable
private data class GeminiGenerationConfig(
    @SerialName("responseMimeType") val responseMimeType: String
)

@Serializable
private data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

@Serializable
private data class GeminiCandidate(
    val content: GeminiContent
)
