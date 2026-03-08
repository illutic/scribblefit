package com.scribblefit.feature.canvas.domain.usecase

import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ledger.domain.model.ExerciseHistory
import com.scribblefit.feature.ledger.domain.model.SetHistory
import com.scribblefit.feature.ledger.domain.model.WorkoutHistory
import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import java.util.UUID

/**
 * Finalizes the parsed workout session into the permanent workout ledger.
 */
class ConfirmWorkoutUseCase(
    private val ledgerRepository: LedgerRepository
) {
    suspend operator fun invoke(workout: ParsedWorkout) {
        val workoutHistory = WorkoutHistory(
            id = UUID.randomUUID().toString(),
            date = System.currentTimeMillis(),
            location = workout.location,
            totalVolume = workout.exercises.sumOf { exercise ->
                exercise.sets.sumOf { it.weight * it.reps }
            },
            exercises = workout.exercises.map { exercise ->
                ExerciseHistory(
                    canonicalName = exercise.canonicalName,
                    sets = exercise.sets.map { set ->
                        SetHistory(set.weight, set.reps, set.rpe, set.notes)
                    }
                )
            }
        )

        ledgerRepository.logWorkout(workoutHistory)
    }
}
