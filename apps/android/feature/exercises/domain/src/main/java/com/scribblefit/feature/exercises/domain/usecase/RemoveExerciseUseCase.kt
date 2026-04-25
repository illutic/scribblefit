package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RemoveExerciseUseCase(
    private val repository: ExerciseRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(exerciseId: Long): Result<Unit> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            repository.deleteExercise(exerciseId)
        }
    }
}
