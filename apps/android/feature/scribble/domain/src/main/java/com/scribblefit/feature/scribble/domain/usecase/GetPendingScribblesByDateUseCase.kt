package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetPendingScribblesByDateUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(date: CurrentDate): Flow<List<Scribble>> {
        return scribbleRepository
            .getScribblesByDate(date.millis)
            .map { scribbles -> scribbles.filter { it.status == ScribbleStatus.PENDING } }
            .flowOn(coroutineDispatcher)
    }
}
