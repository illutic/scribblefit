package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GetExerciseAIInsightUseCase(
    private val llmEngine: LLMEngine,
    private val exerciseRepository: ExerciseRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        .withZone(ZoneId.systemDefault())

    suspend operator fun invoke(dateLookBehind: Long = 7): Result<AIInsight> {
        val currentDate = CurrentDate(LocalDate.now())
        val startDate = CurrentDate(LocalDate.now().minusDays(dateLookBehind))

        val history: List<Exercise> = exerciseRepository.getExercisesInRange(
            startDate = startDate.startOfDayInMillis,
            endDate = currentDate.startOfDayInMillis
        )

        if (history.isEmpty()) {
            return Result.failure(Exception("No history available to generate insights"))
        }

        val historyContext = history.joinToString("\n") { exercise ->
            val date = dateFormatter.format(Instant.ofEpochMilli(exercise.createdAt))
            val sets = exercise.sets.joinToString(", ") { "${it.weight}x${it.reps}" }
            "$date: $sets"
        }

        return llmEngine.generateExerciseInsight(historyContext)
    }
}
