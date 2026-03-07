package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import com.scribblefit.feature.ai.domain.repository.AnalysisRepository

/**
 * UseCase to trigger a background analysis refresh.
 * Typically called after a workout is saved or on a daily schedule.
 */
class AnalyzeWorkoutsUseCase(
    private val repository: AnalysisRepository,
    private val engine: AnalysisEngine,
) {
    suspend fun refreshHomeSuggestion(workoutHistoryContext: String) {
        engine.generateSuggestion(workoutHistoryContext).onSuccess {
            repository.saveHomeSuggestion(it)
        }
    }

    suspend fun refreshSummary(period: SummaryPeriod, serializedData: String) {
        engine.generateSummary(period, serializedData).onSuccess {
            repository.saveSummary(it)
        }
    }

    suspend fun refreshExerciseInsight(exerciseId: String, exerciseName: String, historyData: String) {
        engine.generateExerciseInsight(exerciseName, historyData).onSuccess {
            repository.saveExerciseInsight(it.copy(exerciseId = exerciseId))
        }
    }
}
