package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.ConfigRepository
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
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta"

private const val SUGGESTION_PROMPT = """You are ScribbleFit AI, a fitness analysis assistant.
Generate one actionable training suggestion based on the workout context below.
Output ONLY this JSON (no markdown, no extra text):
{"text":"suggestion text","emoji":"emoji","type":"RECOVERY|PATTERN|MILESTONE|REST"}
type must be exactly one of: RECOVERY, PATTERN, MILESTONE, REST"""

private const val SUMMARY_PROMPT = """You are ScribbleFit AI, a fitness analysis assistant.
Analyze the workout data below and generate a training summary.
Output ONLY this JSON (no markdown, no extra text):
{"summaryText":"2-3 sentence summary","highlights":["highlight 1","highlight 2"],"muscleDistribution":[{"muscleGroup":"name","volumePercentage":number}],"focusArea":"primary muscle group","volumeDelta":number}
muscleDistribution percentages must sum to 100. volumeDelta is percentage change vs previous period."""

private const val INSIGHT_PROMPT = """You are ScribbleFit AI, a fitness analysis assistant.
Analyze the exercise history below and generate a performance insight.
Output ONLY this JSON (no markdown, no extra text):
{"estimated1RM":number,"prDetected":true|false,"trendDirection":"IMPROVING|STABLE|PLATEAUED|DECLINING","breakdownText":"2-3 sentence analysis"}
Use Epley formula (weight * (1 + reps/30)) for 1RM estimate. trendDirection must be exactly one of: IMPROVING, STABLE, PLATEAUED, DECLINING"""

class GeminiAIEngine(
    private val httpClient: HttpClient,
    private val secureKeyStorage: SecureKeyStorage,
    private val configRepository: ConfigRepository,
    private val json: Json
) : LLMEngine, AnalysisEngine {
    private val config = configRepository.getConfig()

    override suspend fun parseWorkout(rawText: String): ParsedWorkoutResult {
        val prompt = config.first()?.promptText
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

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> = try {
        val apiKey = secureKeyStorage.getApiKey()
            ?: return Result.failure(Exception("No API key"))
        val responseText = callGemini(apiKey, "$SUGGESTION_PROMPT\n\nContext:\n$context")
        val dto = json.decodeFromString<SuggestionResponseDto>(responseText)
        Result.success(
            AnalysisSuggestion(
                text = dto.text,
                emoji = dto.emoji,
                type = dto.type,
                timestamp = System.currentTimeMillis()
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary> = try {
        val apiKey = secureKeyStorage.getApiKey()
            ?: return Result.failure(Exception("No API key"))
        val responseText = callGemini(apiKey, "$SUMMARY_PROMPT\n\nPeriod: ${period.name}\nData:\n$workoutData")
        val dto = json.decodeFromString<SummaryResponseDto>(responseText)
        Result.success(
            AnalysisSummary(
                period = period,
                summaryText = dto.summaryText,
                highlights = dto.highlights,
                muscleDistribution = dto.muscleDistribution,
                focusArea = dto.focusArea,
                volumeDelta = dto.volumeDelta,
                timestamp = System.currentTimeMillis()
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight> = try {
        val apiKey = secureKeyStorage.getApiKey()
            ?: return Result.failure(Exception("No API key"))
        val responseText = callGemini(apiKey, "$INSIGHT_PROMPT\n\nExercise: $exerciseName\nHistory:\n$historyData")
        val dto = json.decodeFromString<InsightResponseDto>(responseText)
        Result.success(
            ExerciseInsight(
                exerciseId = exerciseName,
                estimated1RM = dto.estimated1RM,
                prDetected = dto.prDetected,
                trendDirection = dto.trendDirection,
                breakdownText = dto.breakdownText,
                timestamp = System.currentTimeMillis()
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

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
        val model = config.first()?.preferredModel ?: ""
        val response = httpClient.post("$GEMINI_BASE_URL/models/$model:generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val geminiResponse = response.body<GeminiResponse>()
        return geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
    }
}
