package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleError
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

class ManualEditScribbleUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend fun updateExerciseName(scribbleId: Long, exerciseId: Long, newName: String) = withContext(coroutineDispatcher) {
        val scribble = scribbleRepository.getScribble(scribbleId).firstOrNull() ?: throw ScribbleError.NotFound(scribbleId)
        val updatedExercises = scribble.exercises.map { exercise ->
            if (exercise.id == exerciseId) {
                exercise.copy(canonicalName = newName)
            } else exercise
        }
        scribbleRepository.updateScribbleExercises(scribbleId, updatedExercises)
    }

    suspend fun updateSetWeight(scribbleId: Long, exerciseId: Long, setId: Long, newWeight: Float) = withContext(coroutineDispatcher) {
        val scribble = scribbleRepository.getScribble(scribbleId).firstOrNull() ?: throw ScribbleError.NotFound(scribbleId)
        val updatedExercises = scribble.exercises.map { exercise ->
            if (exercise.id == exerciseId) {
                val updatedSets = exercise.sets.map { set ->
                    if (set.id == setId) {
                        set.copy(weight = newWeight)
                    } else set
                }
                exercise.copy(sets = updatedSets)
            } else exercise
        }
        scribbleRepository.updateScribbleExercises(scribbleId, updatedExercises)
    }

    suspend fun updateSetReps(scribbleId: Long, exerciseId: Long, setId: Long, newReps: Int) = withContext(coroutineDispatcher) {
        val scribble = scribbleRepository.getScribble(scribbleId).firstOrNull() ?: throw ScribbleError.NotFound(scribbleId)
        val updatedExercises = scribble.exercises.map { exercise ->
            if (exercise.id == exerciseId) {
                val updatedSets = exercise.sets.map { set ->
                    if (set.id == setId) {
                        set.copy(reps = newReps)
                    } else set
                }
                exercise.copy(sets = updatedSets)
            } else exercise
        }
        scribbleRepository.updateScribbleExercises(scribbleId, updatedExercises)
    }

    suspend fun deleteSet(scribbleId: Long, exerciseId: Long, setId: Long) = withContext(coroutineDispatcher) {
        val scribble = scribbleRepository.getScribble(scribbleId).firstOrNull() ?: throw ScribbleError.NotFound(scribbleId)
        val updatedExercises = scribble.exercises.map { exercise ->
            if (exercise.id == exerciseId) {
                val updatedSets = exercise.sets.filter { it.id != setId }
                exercise.copy(sets = updatedSets)
            } else exercise
        }
        scribbleRepository.updateScribbleExercises(scribbleId, updatedExercises)
    }
}
