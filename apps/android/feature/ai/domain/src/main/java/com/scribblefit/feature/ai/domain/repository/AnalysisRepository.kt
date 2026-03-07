package com.scribblefit.feature.ai.domain.repository

import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import kotlinx.coroutines.flow.Flow

interface AnalysisRepository {
    // Read cached insights
    fun getHomeSuggestion(): Flow<AnalysisSuggestion?>
    fun getSummary(period: SummaryPeriod): Flow<AnalysisSummary?>
    fun getExerciseInsight(exerciseId: String): Flow<ExerciseInsight?>

    // Cache management (called by UseCases after LLM processing)
    suspend fun saveHomeSuggestion(suggestion: AnalysisSuggestion)
    suspend fun saveSummary(summary: AnalysisSummary)
    suspend fun saveExerciseInsight(insight: ExerciseInsight)
    
    // Manual trigger for refresh logic
    suspend fun clearOldInsights()
}
