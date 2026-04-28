package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.Calculations
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.core.model.ExerciseHistorySession
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Use case to fetch the complete history of a specific exercise.
 */
class GetExerciseHistoryUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val formatExerciseSummaryUseCase: FormatExerciseSummaryUseCase,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        exerciseName: String,
        weightUnit: Weight
    ): Result<List<ExerciseHistorySession>> = withContext(coroutineDispatcher) {
        runCatching {
            val history = exerciseRepository.getExercisesByName(exerciseName)
                .sortedByDescending { it.createdAt }

            if (history.isEmpty()) return@runCatching emptyList()

            // Calculate max weight and volume across all time to identify PBs
            val allTimeMaxWeight = history.flatMap { it.sets }.maxOfOrNull { it.weight ?: 0f } ?: 0f
            val allTimeMaxVolume = history.maxOfOrNull { ex ->
                ex.sets.sumOf { set -> Calculations.calculateVolume(set.weight, set.reps).toDouble() }.toFloat()
            } ?: 0f

            history.map { exercise ->
                val sessionVolume = exercise.sets.sumOf { set ->
                    Calculations.calculateVolume(set.weight, set.reps).toDouble()
                }.toFloat()
                
                val sessionMaxWeight = exercise.sets.maxOfOrNull { it.weight ?: 0f } ?: 0f

                // A session is a PB if it contains the all-time max weight or achieves all-time max volume
                // Note: In a real app, you might want more nuanced PB logic (e.g. per rep range)
                val isPB = (sessionMaxWeight >= allTimeMaxWeight && allTimeMaxWeight > 0f) || 
                           (sessionVolume >= allTimeMaxVolume && allTimeMaxVolume > 0f)

                // We need the scribbleId to navigate back to the original workout.
                // The repository should provide this. 
                // Currently Exercise model doesn't have scribbleId, but ExerciseEntity does.
                // I'll check if I need to update the Exercise domain model or if I can get it another way.
                
                ExerciseHistorySession(
                    exercise = exercise,
                    totalVolume = sessionVolume,
                    maxWeight = sessionMaxWeight,
                    summary = formatExerciseSummaryUseCase(exercise, weightUnit),
                    isPersonalBest = isPB,
                    scribbleId = exercise.scribbleId
                )
            }
        }
    }
}
