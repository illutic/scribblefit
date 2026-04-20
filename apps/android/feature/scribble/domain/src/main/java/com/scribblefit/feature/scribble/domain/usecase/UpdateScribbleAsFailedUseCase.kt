package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleError
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class UpdateScribbleAsFailedUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Long): Result<Unit> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                val scribble =
                    scribbleRepository.getScribbleWithExercises(id).firstOrNull()
                        ?: throw ScribbleError.NotFound(id)

                updateScribbleStatusToFailed(scribble)
            }
        }

    private suspend fun updateScribbleStatusToFailed(scribble: Scribble) {
        scribbleRepository.updateScribble(
            scribble.copy(
                status = ScribbleStatus.FAILED,
            ),
        )
    }
}
