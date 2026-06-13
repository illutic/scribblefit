package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RemoveExerciseUseCase(
    private val repository: ExerciseRepository,
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(exerciseId: Long): Result<Unit> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            val exercise = repository.getExerciseById(exerciseId) ?: return@runCatchingWithCancellation
            val scribbleId = exercise.scribbleId
            
            repository.deleteExercise(exerciseId)
            
            val remainingExercises = repository.getExercisesForScribble(scribbleId)
            if (remainingExercises.isEmpty()) {
                scribbleRepository.deleteScribble(scribbleId)
            }
        }
    }
}
