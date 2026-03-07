package com.scribblefit.feature.ai.domain.engine

import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.SummaryPeriod

/**
 * Interface for the AI Analysis Engine.
 * Responsible for high-level reasoning and summarization using LLMs.
 */
interface AnalysisEngine {
    /**
     * Generates a proactive suggestion for the home screen.
     * @param context Recent workout history summary.
     */
    suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion>

    /**
     * Generates a summary for a specific period.
     * @param period The timeframe (W/M/Y).
     * @param workoutData Serialized workout logs for that period.
     */
    suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary>

    /**
     * Generates deep insights for a specific exercise.
     * @param exerciseName The canonical name of the exercise.
     * @param historyData Serialized set history for this exercise.
     */
    suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight>
}
