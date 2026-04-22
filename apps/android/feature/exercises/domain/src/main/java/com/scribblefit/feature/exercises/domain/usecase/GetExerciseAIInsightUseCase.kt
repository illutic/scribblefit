package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.ExerciseHistorySession
import com.scribblefit.core.model.ExercisePerformanceInsight
import com.scribblefit.feature.ai.domain.LLMEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GetExerciseAIInsightUseCase(
    private val llmEngine: LLMEngine
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        .withZone(ZoneId.systemDefault())

    suspend operator fun invoke(history: List<ExerciseHistorySession>): Result<ExercisePerformanceInsight> {
        if (history.isEmpty()) {
            return Result.failure(Exception("No history available to generate insights"))
        }

        val historyContext = history.take(5).joinToString("\n") { session ->
            val date = dateFormatter.format(Instant.ofEpochMilli(session.date))
            val sets = session.exercise.sets.joinToString(", ") { "${it.weight}x${it.reps}" }
            "$date: $sets"
        }

        return llmEngine.generateExerciseInsight(historyContext)
    }
}
