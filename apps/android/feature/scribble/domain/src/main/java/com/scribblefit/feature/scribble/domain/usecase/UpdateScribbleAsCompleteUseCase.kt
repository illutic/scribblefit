package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.exercises.domain.usecase.MarkExerciseAsCompleteUseCase
import com.scribblefit.feature.scribble.domain.ScribbleNotFoundException
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class UpdateScribbleAsCompleteUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val markExerciseAsCompleteUseCase: MarkExerciseAsCompleteUseCase,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Long): Result<Unit> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            val scribble = scribbleRepository.getScribbleWithExercises(id).firstOrNull()
                ?: throw ScribbleNotFoundException(id)

            updateScribbleExercisesAsComplete(scribble)
            updateScribbleStatusToComplete(scribble)
        }
    }

    private suspend fun updateScribbleExercisesAsComplete(scribble: Scribble) {
        scribble.exercises.forEach { exercise ->
            markExerciseAsCompleteUseCase(exercise).getOrThrow()
        }
    }

    private suspend fun updateScribbleStatusToComplete(scribble: Scribble) {
        scribbleRepository.updateScribble(
            scribble.copy(
                status = ScribbleStatus.COMPLETED
            )
        )
    }
}
