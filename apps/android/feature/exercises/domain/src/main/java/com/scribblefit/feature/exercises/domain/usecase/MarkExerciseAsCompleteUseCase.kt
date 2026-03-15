package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.Exercise
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MarkExerciseAsCompleteUseCase(
    private val updateExerciseUseCase: UpdateExerciseUseCase,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(exercise: Exercise): Result<Unit> =
        withContext(coroutineDispatcher) {
            updateExerciseUseCase(exercise.copy(isDraft = false))
        }
}
