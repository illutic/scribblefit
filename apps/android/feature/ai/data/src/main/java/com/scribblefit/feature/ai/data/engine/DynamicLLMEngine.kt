package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.ConfigRepository
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.ai.domain.model.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Named

class DynamicLLMEngine @Inject constructor(
    @param:Named("openai") private val openAIEngine: LLMEngine,
    @param:Named("gemini") private val geminiAIEngine: LLMEngine,
    @param:Named("proxy") private val proxyEngine: LLMEngine,
    private val localAIEngine: LocalAIEngine,
    private val configRepository: ConfigRepository
) : LLMEngine, AnalysisEngine {

    override suspend fun parseWorkout(rawText: String): ParsedWorkoutResult {
        val config = configRepository.getConfig().first()
        val preferredProvider = config?.preferredLlmProvider ?: LLMProvider.PROXY
        val engine = getEngine(preferredProvider)
        val result = engine.parseWorkout(rawText)
        return result
    }

    private fun getEngine(preferred: LLMProvider): LLMEngine {
        return when (preferred) {
            LLMProvider.OPENAI -> openAIEngine
            LLMProvider.GEMINI -> geminiAIEngine
            LLMProvider.PROXY -> proxyEngine
            LLMProvider.LOCAL -> localAIEngine
        }
    }

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> {
        val config = configRepository.getConfig().first()
        val preferredProvider = config?.preferredLlmProvider ?: LLMProvider.PROXY
        val engine =
            getEngine(preferredProvider) as? AnalysisEngine ?: error("Analysis Engine not found")
        val result = engine.generateSuggestion(context)
        if (result.isSuccess) return result
        val lastError = result.exceptionOrNull()
        return Result.failure(lastError ?: Exception("All analysis engines failed"))
    }

    override suspend fun generateSummary(
        period: SummaryPeriod,
        workoutData: String
    ): Result<AnalysisSummary> {
        val config = configRepository.getConfig().first()
        val preferredProvider = config?.preferredLlmProvider ?: LLMProvider.PROXY
        val engine =
            getEngine(preferredProvider) as? AnalysisEngine ?: error("Analysis Engine not found")

        val result = engine.generateSummary(period, workoutData)
        if (result.isSuccess) return result
        val lastError = result.exceptionOrNull()
        return Result.failure(lastError ?: Exception("All analysis engines failed"))
    }

    override suspend fun generateExerciseInsight(
        exerciseName: String,
        historyData: String
    ): Result<ExerciseInsight> {
        val config = configRepository.getConfig().first()
        val preferredProvider = config?.preferredLlmProvider ?: LLMProvider.PROXY
        val engine = getEngine(preferredProvider) as? AnalysisEngine ?: error("No Analysis Engine")
        val result = engine.generateExerciseInsight(exerciseName, historyData)
        if (result.isSuccess) return result
        val lastError = result.exceptionOrNull()
        return Result.failure(lastError ?: Exception("All analysis engines failed"))
    }

}
