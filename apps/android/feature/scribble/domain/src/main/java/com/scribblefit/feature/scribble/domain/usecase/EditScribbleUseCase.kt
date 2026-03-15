package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.EmptyScribbleTextException
import com.scribblefit.feature.scribble.domain.ScribbleNotFoundException
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class EditScribbleUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(id: Long, newText: String): Result<Unit> =
        runCatchingWithCancellation {
            withContext(coroutineDispatcher) {
                val existing = scribbleRepository.getScribble(id).firstOrNull()
                when {
                    existing == null -> throw ScribbleNotFoundException(id)
                    newText.isBlank() -> throw EmptyScribbleTextException()
                }
                scribbleRepository.updateScribble(
                    existing.copy(
                        rawText = newText,
                        status = ScribbleStatus.RAW
                    )
                )
            }
        }
}
