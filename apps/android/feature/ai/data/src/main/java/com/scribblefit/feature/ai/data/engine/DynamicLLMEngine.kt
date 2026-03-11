package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

internal class DynamicLLMEngine(
    private val openAIEngine: LLMEngine,
    private val geminiEngine: LLMEngine,
    private val localEngine: LocalAIEngine,
    configRepository: ConfigRepository,
    coroutineDispatcher: CoroutineDispatcher
) : LLMEngine, AnalysisEngine,
    CoroutineScope by CoroutineScope(coroutineDispatcher + CoroutineName("DynamicLLMEngine")) {
    private val activeEngine = configRepository.config.map {
        when (it.preferredLlmProvider) {
            LLMProvider.OPENAI -> openAIEngine
            LLMProvider.GEMINI -> geminiEngine
            else -> localEngine
        }
    }.stateIn(this, SharingStarted.Eagerly, localEngine)

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult> =
        withContext(coroutineContext) {
            activeEngine.value.parseWorkout(rawText)
        }

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> =
        withContext(coroutineContext) {
            ensureAnalysisEngine().generateSuggestion(context)
        }

    override suspend fun generateSummary(
        period: SummaryPeriod,
        workoutData: String
    ): Result<AnalysisSummary> = withContext(coroutineContext) {
        ensureAnalysisEngine().generateSummary(
            period,
            workoutData
        )
    }

    override suspend fun generateExerciseInsight(
        exerciseId: String
    ): Result<ExerciseInsight> = withContext(coroutineContext) {
        ensureAnalysisEngine().generateExerciseInsight(exerciseId)
    }

    private fun ensureAnalysisEngine() =
        activeEngine.value as? AnalysisEngine ?: error("Active engine does not support analysis")

}
