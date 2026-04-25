package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.feature.sets.domain.SetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Appends a new set to an exercise.
 */
class AddSetToExerciseUseCase(
    private val repository: SetRepository,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(exercise: Exercise): Result<Long> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                val exerciseId = exercise.id
                val nextSetNumber = (exercise.sets.maxOfOrNull { it.setNumber } ?: 0) + 1
                val newSet = Set(
                    id = 0,
                    setNumber = nextSetNumber,
                    reps = 0,
                    weight = 0f
                )
                repository.addSet(exerciseId, newSet)
            }
        }
}
