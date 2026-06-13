package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.WeeklyStats
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId

data class CalculateWeeklyStatsUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(exerciseId: Long): Result<WeeklyStats> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                val exercise = exerciseRepository.getExerciseById(exerciseId)
                    ?: throw IllegalArgumentException("Exercise with ID $exerciseId not found")

                val exerciseDate = Instant.ofEpochMilli(exercise.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val startDate = CurrentDate(exerciseDate.minusDays(7))
                val endDateInMillis = CurrentDate(exerciseDate.plusDays(1)).startOfDayInMillis - 1

                val exercises = exerciseRepository.getExercisesInRange(
                    startDate = startDate.startOfDayInMillis,
                    endDate = endDateInMillis
                ).filter { it.canonicalName == exercise.canonicalName }

                if (exercises.isEmpty()) {
                    throw IllegalArgumentException("No exercises found for the given ID in the past week")
                }

                val totalVolume = exercises.sumOf { exercise ->
                    exercise.sets.sumOf { set ->
                        ((set.weight ?: 0f) * set.reps).toDouble()
                    }
                }.toFloat()

                val maxWeight = exercises.flatMap { it.sets }
                    .mapNotNull { it.weight }
                    .maxOrNull() ?: 0f

                WeeklyStats(
                    sessions = exercises.size,
                    totalVolume = totalVolume,
                    maxWeight = maxWeight
                )
            }
        }
}
