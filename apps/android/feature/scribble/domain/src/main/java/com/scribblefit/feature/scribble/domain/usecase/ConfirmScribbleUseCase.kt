package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.scribble.domain.error.ScribbleError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ConfirmScribbleUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(scribble: Scribble) = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            if (scribble.status != ScribbleStatus.SUCCESS) {
                throw ScribbleError.InvalidStatus(scribble.status)
            }

            scribbleRepository.updateScribble(scribble.copy(status = ScribbleStatus.COMPLETED))
        }
    }
}
