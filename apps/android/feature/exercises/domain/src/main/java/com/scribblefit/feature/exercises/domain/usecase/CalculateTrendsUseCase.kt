package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.ExerciseTrends
import com.scribblefit.core.model.TrendDirection
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CalculateTrendsUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val calculateWeeklyStatsUseCase: CalculateWeeklyStatsUseCase,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(exerciseId: Long): Result<ExerciseTrends> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                val exercise = exerciseRepository.getExerciseById(exerciseId)
                    ?: error("Exercise with ID $exerciseId not found")

                val exercises = exerciseRepository.getExercisesByName(exercise.canonicalName)

                val trends = calculateWeeklyStatsUseCase(exerciseId).getOrThrow()

                if (exercises.isEmpty()) {
                    error("No exercises found for the given ID")
                }

                val sortedExercises = exercises.sortedBy { it.createdAt }

                val estimated1RM = calculateEstimated1RM(exercise)
                val intensity = calculateIntensity(estimated1RM, trends.maxWeight)
                val previousMax =
                    sortedExercises.dropLast(1).maxOfOrNull { calculateEstimated1RM(it) } ?: 0f
                val improvement = calculateImprovement(estimated1RM, previousMax)
                val trendDirection = determineTrendDirection(improvement)

                val lastVolume = calculateVolume(exercise)
                val previousSession = sortedExercises.lastOrNull { it.id != exercise.id }
                val lastVolumeTrend = if (previousSession == null) {
                    TrendDirection.STABLE
                } else {
                    determineVolumeTrendDirection(lastVolume, calculateVolume(previousSession))
                }

                ExerciseTrends(
                    estimated1RM = estimated1RM,
                    intensity = intensity,
                    improvement = improvement,
                    trendDirection = trendDirection,
                    lastVolume = lastVolume,
                    lastVolumeTrend = lastVolumeTrend
                )
            }
        }

    private fun calculateImprovement(currentMax: Float, previousMax: Float): Float {
        return if (previousMax > 0) (currentMax - previousMax) / previousMax else 0f
    }

    private fun determineTrendDirection(improvement: Float): TrendDirection {
        return when {
            improvement > IMPROVEMENT_THRESHOLD -> TrendDirection.IMPROVING
            improvement < -IMPROVEMENT_THRESHOLD -> TrendDirection.DECLINING
            else -> TrendDirection.STABLE
        }
    }

    private fun calculateIntensity(estimated1RM: Float, maxWeight: Float): Float {
        return if (maxWeight > 0) estimated1RM / maxWeight else 0f
    }

    private fun calculateVolume(exercise: Exercise): Float {
        return exercise.sets.sumOf { set ->
            ((set.weight ?: 0f) * set.reps).toDouble()
        }.toFloat()
    }

    private fun determineVolumeTrendDirection(current: Float, previous: Float): TrendDirection {
        return when {
            current > previous -> TrendDirection.IMPROVING
            current < previous -> TrendDirection.DECLINING
            else -> TrendDirection.STABLE
        }
    }

    private fun calculateEstimated1RM(exercise: Exercise): Float {
        return exercise.sets.maxOfOrNull { set ->
            val weight = set.weight ?: 0f
            val reps = set.reps
            if (reps > 0) weight * (1 + reps / ESTIMATED_1RM_REPS_FACTOR) else weight
        } ?: 0f
    }

    companion object {
        private const val IMPROVEMENT_THRESHOLD = 0.05f
        private const val ESTIMATED_1RM_REPS_FACTOR = 30f
    }
}