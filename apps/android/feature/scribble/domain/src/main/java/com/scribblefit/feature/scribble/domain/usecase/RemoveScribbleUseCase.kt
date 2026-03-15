package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.exercises.domain.usecase.RemoveExerciseUseCase
import com.scribblefit.feature.scribble.domain.ScribbleNotFoundException
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class RemoveScribbleUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val removeExerciseUseCase: RemoveExerciseUseCase,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Long): Result<Unit> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            val scribble = scribbleRepository.getScribbleWithExercises(id).firstOrNull()
                ?: throw ScribbleNotFoundException(id)

            removeScribbleExercises(scribble)
            removeScribble(scribble)
        }
    }

    private suspend fun removeScribbleExercises(scribble: Scribble) {
        scribble.exercises.forEach { exercise ->
            removeExerciseUseCase(exercise).getOrThrow()
        }
    }

    private suspend fun removeScribble(scribble: Scribble) {
        scribbleRepository.deleteScribble(scribble.id)
    }
}