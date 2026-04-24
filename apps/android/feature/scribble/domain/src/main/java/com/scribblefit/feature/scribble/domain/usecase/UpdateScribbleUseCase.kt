package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class UpdateScribbleUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(scribble: Scribble) =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                checkNotNull(scribbleRepository.getScribble(scribble.id).firstOrNull()) {
                    "Scribble with id ${scribble.id} not found"
                }

                scribbleRepository.updateScribble(scribble)
            }
        }

}