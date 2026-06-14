package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.exercises.domain.usecase.UpdateExerciseUseCase
import com.scribblefit.feature.sets.domain.usecase.RemoveSetUseCase

/**
 * Updates exercises and sets directly in the persistent store.
 */
class UpdateScribbleExerciseUseCase(
    private val updateExerciseUseCase: UpdateExerciseUseCase,
    private val removeSetUseCase: RemoveSetUseCase
) {
    suspend fun updateExerciseName(scribble: Scribble, exerciseId: Long, newName: String) {
        val exercise = scribble.exercises.find { it.id == exerciseId } ?: return
        updateExerciseUseCase(exercise.copy(canonicalName = newName))
    }

    suspend fun updateSetWeight(
        scribble: Scribble,
        exerciseId: Long,
        setId: Long,
        newWeight: String
    ) {
        val weight = newWeight.toFloatOrNull() ?: return
        val exercise = scribble.exercises.find { it.id == exerciseId } ?: return
        val newExercise = exercise.copy(
            sets = exercise.sets.map {
                if (it.id == setId) it.copy(weight = weight) else it
            }
        )
        updateExerciseUseCase(newExercise)
    }

    suspend fun updateSetReps(scribble: Scribble, exerciseId: Long, setId: Long, newReps: String) {
        val reps = newReps.toIntOrNull() ?: return
        val exercise = scribble.exercises.find { it.id == exerciseId } ?: return
        val newExercise = exercise.copy(
            sets = exercise.sets.map {
                if (it.id == setId) it.copy(reps = reps) else it
            }
        )
        updateExerciseUseCase(newExercise)
    }

    suspend fun deleteSet(scribble: Scribble, exerciseId: Long, setId: Long) {
        val exercise = scribble.exercises.find { it.id == exerciseId } ?: return
        val newExercise = exercise.copy(
            sets = exercise.sets.filterNot { it.id == setId }
        )
        removeSetUseCase(setId)
        updateExerciseUseCase(newExercise)
    }
}
