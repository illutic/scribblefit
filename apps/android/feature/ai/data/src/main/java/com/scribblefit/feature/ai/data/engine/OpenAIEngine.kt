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
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val OPENAI_BASE_URL = "https://api.openai.com/v1"
private const val DEFAULT_MODEL = "gpt-4o-mini"

class OpenAIEngine(
    private val httpClient: HttpClient,
    private val secureKeyStorage: SecureKeyStorage,
    private val json: Json,
    private val prompt: String,
    private val preferredModel: String = ""
) : LLMEngine, AnalysisEngine {

    override suspend fun parseWorkout(rawText: String): ParsedWorkoutResult {
        val startMs = System.currentTimeMillis()
        return try {
            val apiKey = secureKeyStorage.getApiKey() ?: return ParsedWorkoutResult(
                workout = null, rawText = rawText, status = ParsingStatus.FAILURE, error = "No API key"
            )
            val model = preferredModel.ifBlank { DEFAULT_MODEL }
            val responseText = callOpenAI(apiKey, model, "$prompt\n\nInput: $rawText")
            val workout = json.decodeFromString<ParsedWorkout>(responseText)
            ParsedWorkoutResult(
                workout = workout,
                rawText = rawText,
                status = ParsingStatus.SUCCESS,
                modelUsed = model,
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
        Result.failure(NotImplementedError("OpenAI analysis not yet implemented"))

    override suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary> =
        Result.failure(NotImplementedError("OpenAI analysis not yet implemented"))

    override suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight> =
        Result.failure(NotImplementedError("OpenAI analysis not yet implemented"))

    private suspend fun callOpenAI(apiKey: String, model: String, userPrompt: String): String {
        @Serializable
        data class Message(val role: String, val content: String)
        @Serializable
        data class ResponseFormat(val type: String)
        @Serializable
        data class ChatRequest(val model: String, val messages: List<Message>, val response_format: ResponseFormat)
        @Serializable
        data class Choice(val message: Message)
        @Serializable
        data class ChatResponse(val choices: List<Choice>)

        val request = ChatRequest(
            model = model,
            messages = listOf(Message("user", userPrompt)),
            response_format = ResponseFormat("json_object")
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
