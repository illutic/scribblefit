package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import java.time.ZoneOffset

class GetScribblesByDateUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(date: Flow<LocalDate>): Flow<List<Scribble>> =
        date.flatMapMerge {
            val startOfDayMillis = it.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            scribbleRepository
                .getScribblesByDate(startOfDayMillis)
                .flowOn(coroutineDispatcher)
        }
}
