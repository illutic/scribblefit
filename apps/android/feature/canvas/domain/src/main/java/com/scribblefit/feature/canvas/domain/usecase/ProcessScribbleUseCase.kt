package com.scribblefit.feature.canvas.domain.usecase

import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ProcessScribbleUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend fun execute(rawText: String) = withContext(coroutineDispatcher) {
        scribbleRepository.enqueueScribble(rawText)
    }
}
