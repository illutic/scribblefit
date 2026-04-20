package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.EmptyScribbleTextException
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.scribble.domain.error.ScribbleError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class AddRawScribbleUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(newText: String, date: LocalDate = LocalDate.now()): Result<Unit> =
        runCatchingWithCancellation {
            withContext(coroutineDispatcher) {
                if (newText.isBlank()) throw ScribbleError.EmptyText

                scribbleRepository.insertScribble(
                    Scribble(
                        id = 0L,
                        rawText = newText,
                        status = ScribbleStatus.PENDING,
                        createdAt = getCurrentDateInMillis(date)
                    )
                )
            }
        }

    private fun getCurrentDateInMillis(date: LocalDate): Long = date
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

