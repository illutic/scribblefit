package com.scribblefit.feature.analytics.domain.usecase

import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import com.scribblefit.feature.analytics.domain.repository.AnalysisRepository

class AnalyzeWorkoutsUseCase(
    private val engine: AnalysisEngine,
    private val repository: AnalysisRepository
) {
    suspend fun refreshHomeSuggestion(context: String) {
        engine.generateSuggestion(context).onSuccess { repository.saveHomeSuggestion(it) }
    }

    suspend fun refreshSummary(period: SummaryPeriod, workoutData: String) {
        engine.generateSummary(period, workoutData).onSuccess { repository.saveSummary(it) }
    }

    suspend fun refreshExerciseInsight(exerciseId: String, exerciseName: String, historyData: String) {
        engine.generateExerciseInsight(exerciseName, historyData).onSuccess { insight ->
            repository.saveExerciseInsight(insight.copy(exerciseId = exerciseId))
        }
    }
}
