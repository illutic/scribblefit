package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Use case to remove an exercise from a scribble.
 * If the exercise was the last one in the scribble, the scribble is also removed.
 */
class RemoveExerciseFromScribbleUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val removeScribbleUseCase: RemoveScribbleUseCase,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(exerciseId: Long, scribbleId: Long): Result<Unit> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            exerciseRepository.deleteExercise(exerciseId)
            val remainingExercises = exerciseRepository.getExercisesForScribble(scribbleId)
            if (remainingExercises.isEmpty()) {
                removeScribbleUseCase(scribbleId)
            }
        }
    }
}
