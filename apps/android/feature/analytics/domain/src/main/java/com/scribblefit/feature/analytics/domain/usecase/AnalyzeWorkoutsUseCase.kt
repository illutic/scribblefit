package com.scribblefit.feature.analytics.domain.usecase

import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import com.scribblefit.feature.analytics.domain.repository.AnalysisRepository

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

    suspend fun refreshExerciseInsight(
        exerciseId: String,
        exerciseName: String,
        historyData: String
    ) {
        engine.generateExerciseInsight(exerciseName, historyData).onSuccess {
            repository.saveExerciseInsight(it.copy(exerciseId = exerciseId))
        }
    }
}
