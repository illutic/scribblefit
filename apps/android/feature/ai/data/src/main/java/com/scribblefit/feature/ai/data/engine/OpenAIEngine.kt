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
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val OPENAI_BASE_URL = "https://api.openai.com/v1"

internal class OpenAIEngine(
    private val httpClient: HttpClient,
    private val secureKeyStorage: SecureKeyStorage,
    private val json: Json,
    private val configRepository: ConfigRepository
) : LLMEngine, AnalysisEngine {
    private val config get() = configRepository.config.value
    private val apiKey get() = secureKeyStorage.getApiKey() ?: error("No API key provided")

    @InternalSerializationApi
    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult> = runCatching {
        val startMs = System.currentTimeMillis()
        val model = config.preferredModel ?: error("No Model selected")
        val responseText = callOpenAI(apiKey, model, "${config.parsePrompt}\n\nInput: $rawText")
        val workout = json.decodeFromString<WorkoutDto>(responseText)
        ParsedWorkoutResult(
            workout = workout.toDomain(),
            rawText = rawText,
            status = ParsingStatus.SUCCESS,
            modelUsed = model,
            processingTimeMs = System.currentTimeMillis() - startMs
        )
    }

    @InternalSerializationApi
    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> =
        runCatching {
            val model = config.preferredModel ?: error("No Model selected")
            val responseText =
                callOpenAI(apiKey, model, "${config.suggestionPrompt}\n\nContext:\n$context")
            val dto = json.decodeFromString<SuggestionResponseDto>(responseText)
            dto.toDomain()
        }

    @InternalSerializationApi
    override suspend fun generateSummary(
        period: SummaryPeriod,
        workoutData: String
    ): Result<AnalysisSummary> = runCatching {
        val model = config.preferredModel ?: error("No model selected")
        val responseText = callOpenAI(
            apiKey,
            model,
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
        val model = config.preferredModel ?: error("No model selected")
        val responseText = callOpenAI(
            apiKey,
            model,
            "${config.insightPrompt}\n\nExercise: $exerciseName\nHistory:\n$historyData"
        )
        val dto = json.decodeFromString<InsightResponseDto>(responseText)
        dto.toDomain(exerciseId)
    }

    @InternalSerializationApi
    private suspend fun callOpenAI(apiKey: String, model: String, userPrompt: String): String {
        @Serializable
        data class Message(val role: String, val content: String)

        @Serializable
        data class ResponseFormat(val type: String)

        @Serializable
        data class ChatRequest(
            val model: String,
            val messages: List<Message>,
            @SerialName("response_format")
            val responseFormat: ResponseFormat
        )

        @Serializable
        data class Choice(val message: Message)

        @Serializable
        data class ChatResponse(val choices: List<Choice>)

        val request = ChatRequest(
            model = model,
            messages = listOf(Message("user", userPrompt)),
            responseFormat = ResponseFormat("json_object")
        )
        val response = httpClient.post("$OPENAI_BASE_URL/chat/completions") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val chatResponse = response.body<ChatResponse>()
        return chatResponse.choices.firstOrNull()?.message?.content ?: ""
    }
}
