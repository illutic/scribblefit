package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RemoveScribbleUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Long): Result<Unit> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            scribbleRepository.deleteScribble(id)
        }
    }
}