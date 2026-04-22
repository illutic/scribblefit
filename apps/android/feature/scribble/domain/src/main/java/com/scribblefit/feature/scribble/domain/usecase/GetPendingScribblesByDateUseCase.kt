package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import java.time.ZoneId

class GetPendingScribblesByDateUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(date: LocalDate): Flow<List<Scribble>> {
        val startOfDayMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return scribbleRepository
            .getPendingScribblesByDate(startOfDayMillis)
            .flowOn(coroutineDispatcher)
    }
}
