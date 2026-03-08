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
import javax.inject.Named

class DynamicLLMEngine(
    @Named("openai") private val openAIEngine: LLMEngine,
    @Named("gemini") private val geminiEngine: LLMEngine,
    private val localEngine: LocalAIEngine,
    private val configRepository: ConfigRepository
) : LLMEngine, AnalysisEngine {

    private suspend fun activeEngine(): LLMEngine {
        val config = configRepository.getConfig().first()
        return when (config?.preferredLlmProvider) {
            LLMProvider.OPENAI -> openAIEngine
            LLMProvider.LOCAL -> localEngine
            else -> geminiEngine
        }
    }

    override suspend fun parseWorkout(rawText: String): ParsedWorkoutResult =
        activeEngine().parseWorkout(rawText)

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> =
        (activeEngine() as? AnalysisEngine)?.generateSuggestion(context)
            ?: Result.failure(IllegalStateException("Active engine does not support analysis"))

    override suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary> =
        (activeEngine() as? AnalysisEngine)?.generateSummary(period, workoutData)
            ?: Result.failure(IllegalStateException("Active engine does not support analysis"))

    override suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight> =
        (activeEngine() as? AnalysisEngine)?.generateExerciseInsight(exerciseName, historyData)
            ?: Result.failure(IllegalStateException("Active engine does not support analysis"))
}
