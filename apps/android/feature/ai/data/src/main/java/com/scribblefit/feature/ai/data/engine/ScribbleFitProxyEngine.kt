package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.ParseRequest
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
import kotlinx.coroutines.flow.first
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ScribbleFitProxyEngine @Inject constructor(
    private val api: ScribbleFitApi,
    private val secureKeyStorage: SecureKeyStorage,
    private val configRepository: ConfigRepository
) : LLMEngine, AnalysisEngine {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun parseWorkout(rawText: String): ParsedWorkoutResult {
        val startTime = System.currentTimeMillis()
        return try {
            val token = secureKeyStorage.getAuthToken()
            val systemPrompt = configRepository.getConfig().first()?.promptText
                ?: error("Prompt is empty. Configuration is not set.")
            val request = ParseRequest(rawText = rawText, prompt = systemPrompt)

            val parsedWorkoutDto = api.parseProxy(request, token)
            val workout = parsedWorkoutDto.toDomain()
            val duration = System.currentTimeMillis() - startTime

            ParsedWorkoutResult(
                workout = workout,
                rawText = rawText,
                status = ParsingStatus.SUCCESS,
                modelUsed = "proxy-orchestrator",
                processingTimeMs = duration
            )
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            ParsedWorkoutResult(
                workout = null,
                rawText = rawText,
                status = ParsingStatus.FAILURE,
                modelUsed = "proxy-orchestrator",
                processingTimeMs = duration,
                error = e.message ?: "Unknown proxy error"
            )
        }
    }

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> =
        runCatching {
            throw UnsupportedOperationException("Proxy analysis not yet supported by backend schema")
        }

    override suspend fun generateSummary(
        period: SummaryPeriod,
        workoutData: String
    ): Result<AnalysisSummary> = runCatching {
        throw UnsupportedOperationException("Proxy analysis not yet supported by backend schema")
    }

    override suspend fun generateExerciseInsight(
        exerciseName: String,
        historyData: String
    ): Result<ExerciseInsight> = runCatching {
        throw UnsupportedOperationException("Proxy analysis not yet supported by backend schema")
    }
}
