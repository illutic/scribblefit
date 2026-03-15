package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateExerciseUseCase(
    private val repository: ExerciseRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(exercise: Exercise): Result<Unit> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            repository.updateExercise(exercise)
        }
    }
}
