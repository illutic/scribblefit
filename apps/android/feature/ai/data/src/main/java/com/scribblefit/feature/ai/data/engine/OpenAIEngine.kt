package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.network.model.ParsedWorkoutDto
import com.scribblefit.feature.ai.data.mapper.ExerciseInsightDto
import com.scribblefit.feature.ai.data.mapper.SuggestionDto
import com.scribblefit.feature.ai.data.mapper.SummaryDto
import com.scribblefit.feature.ai.data.mapper.toDomain
import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.ConfigRepository
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AIParsingException
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

import com.scribblefit.feature.ai.domain.model.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.model.ParsingStatus

class OpenAIEngine(
    private val client: HttpClient,
    private val secureKeyStorage: SecureKeyStorage,
    private val configRepository: ConfigRepository,
    private val json: Json
) : LLMEngine, AnalysisEngine {

    override suspend fun parseWorkout(rawText: String): ParsedWorkoutResult {
        val startTime = System.currentTimeMillis()
        val model = configRepository.getConfig().first()?.preferredModel?.takeIf { it.isNotEmpty() } ?: "gpt-4o-mini"
        return try {
            val apiKey = secureKeyStorage.getApiKey() ?: ""
            val systemPrompt = configRepository.getConfig().first()?.promptText ?: error("Prompt is empty. Configuration is not set.")

            val response = callOpenAIResponse(apiKey, systemPrompt, rawText, model)
            val duration = System.currentTimeMillis() - startTime

            val content = response.output
                .filter { it.type == "message" }
                .firstNotNullOfOrNull { item ->
                    item.content?.firstOrNull { it.type == "text" }?.text
                } ?: throw Exception("Empty response from OpenAI Responses API")

            val reasoning = response.output
                .filter { it.type == "reasoning" }
                .firstNotNullOfOrNull { item ->
                    item.content?.firstOrNull { it.type == "text" }?.text
                }

            val parsedWorkoutDto = json.decodeFromString<ParsedWorkoutDto>(content)
            val workout = parsedWorkoutDto.toDomain()

            ParsedWorkoutResult(
                workout = workout,
                rawText = rawText,
                status = ParsingStatus.SUCCESS,
                modelUsed = model,
                processingTimeMs = duration,
                reasoning = reasoning
            )
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            ParsedWorkoutResult(
                workout = null,
                rawText = rawText,
                status = ParsingStatus.FAILURE,
                modelUsed = model,
                processingTimeMs = duration,
                error = e.message ?: "Unknown error"
            )
        }
    }

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> =
        runCatching {
            val apiKey = secureKeyStorage.getApiKey() ?: ""
            val response = callOpenAIResponse(
                apiKey,
                AnalysisPrompts.getSuggestionPrompt(context),
                "Generate suggestion."
            )
            val content = response.output
                .filter { it.type == "message" }
                .firstNotNullOfOrNull { item ->
                    item.content?.firstOrNull { it.type == "text" }?.text
                } ?: throw Exception("Empty response from OpenAI")
            json.decodeFromString<SuggestionDto>(content).toDomain()
        }

    override suspend fun generateSummary(
        period: SummaryPeriod,
        workoutData: String
    ): Result<AnalysisSummary> = runCatching {
        val apiKey = secureKeyStorage.getApiKey() ?: ""
        val response = callOpenAIResponse(
            apiKey,
            AnalysisPrompts.getSummaryPrompt(period.name, workoutData),
            "Generate summary."
        )
        val content = response.output
            .filter { it.type == "message" }
            .firstNotNullOfOrNull { item ->
                item.content?.firstOrNull { it.type == "text" }?.text
            } ?: throw Exception("Empty response from OpenAI")
        json.decodeFromString<SummaryDto>(content).toDomain(period)
    }

    override suspend fun generateExerciseInsight(
        exerciseName: String,
        historyData: String
    ): Result<ExerciseInsight> = runCatching {
        val apiKey = secureKeyStorage.getApiKey() ?: ""
        val response = callOpenAIResponse(
            apiKey,
            AnalysisPrompts.getExerciseInsightPrompt(exerciseName, historyData),
            "Analyze $exerciseName."
        )
        val content = response.output
            .filter { it.type == "message" }
            .firstNotNullOfOrNull { item ->
                item.content?.firstOrNull { it.type == "text" }?.text
            } ?: throw Exception("Empty response from OpenAI")
        json.decodeFromString<ExerciseInsightDto>(content).toDomain(exerciseName)
    }

    private suspend fun callOpenAIResponse(
        apiKey: String,
        instructions: String,
        userMessage: String,
        model: String = "gpt-4o-mini"
    ): OpenAIResponse {
        val response = client.post("https://api.openai.com/v1/responses") {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(
                OpenAIResponseRequest(
                    model = model,
                    input = JsonPrimitive("$userMessage\n\nOutput in JSON format."),
                    instructions = instructions,
                    text = OpenAITextConfig(
                        format = OpenAIFormatConfig(type = "json_object")
                    )
                )
            )
        }

        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            throw Exception("OpenAI API error: ${response.status} - $errorBody")
        }

        return response.body<OpenAIResponse>()
    }

}

@Serializable
private data class OpenAIResponseRequest(
    val model: String,
    val input: JsonElement,
    val instructions: String? = null,
    val text: OpenAITextConfig? = null
)

@Serializable
private data class OpenAITextConfig(
    val format: OpenAIFormatConfig
)

@Serializable
private data class OpenAIFormatConfig(
    val type: String
)

@Serializable
private data class OpenAIContentPart(
    val type: String,
    val text: String
)

@Serializable
private data class OpenAIResponse(
    val id: String? = null,
    val output: List<OpenAIOutputItem> = emptyList()
)

@Serializable
private data class OpenAIOutputItem(
    val id: String? = null,
    val type: String? = null,
    val content: List<OpenAIContentPart>? = null
)
