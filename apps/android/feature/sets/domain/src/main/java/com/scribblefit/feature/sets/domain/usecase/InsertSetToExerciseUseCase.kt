package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Set
import com.scribblefit.feature.sets.domain.SetNumberNotValidException
import com.scribblefit.feature.sets.domain.SetRepository
import com.scribblefit.feature.sets.domain.SetRepsNotValidException
import com.scribblefit.feature.sets.domain.SetWeightNotValidException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class InsertSetToExerciseUseCase(
    private val repository: SetRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(workoutExerciseId: Long, set: Set): Result<Long> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                when {
                    set.reps <= 0 -> throw SetRepsNotValidException()
                    (set.weight ?: 0f) < 0f -> throw SetWeightNotValidException()
                    set.setNumber <= 0 -> throw SetNumberNotValidException()
                }
                repository.addSet(workoutExerciseId, set)
            }
        }
}
