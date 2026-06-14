package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetScribblesInRangeUseCase(
    private val repository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(startDate: CurrentDate, endDate: CurrentDate): Flow<List<Scribble>> {
        return repository.getScribblesInRange(
            startDate.millis,
            endDate.millis
        ).map { scribbles ->
            scribbles.filter { it.status == ScribbleStatus.COMPLETED }
        }.flowOn(coroutineDispatcher)
    }
}
