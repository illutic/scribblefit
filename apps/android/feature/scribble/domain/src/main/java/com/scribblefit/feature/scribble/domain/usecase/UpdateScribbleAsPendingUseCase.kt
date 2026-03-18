package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleNotFoundException
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class UpdateScribbleAsPendingUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Long): Result<Unit> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                val scribble =
                    scribbleRepository.getScribble(id).firstOrNull()
                        ?: throw ScribbleNotFoundException(id)

                updateScribbleStatusToParsing(scribble)
            }
        }

    private suspend fun updateScribbleStatusToParsing(scribble: Scribble) {
        scribbleRepository.updateScribble(
            scribble.copy(
                status = ScribbleStatus.PARSING,
            ),
        )
    }
}
