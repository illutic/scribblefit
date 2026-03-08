package com.scribblefit.feature.ai.domain.engine

import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.SummaryPeriod

interface AnalysisEngine {
    suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion>
    suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary>
    suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight>
}
