package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetScribblesForDateUseCase(
    private val repository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(date: CurrentDate): Flow<List<Scribble>> {
        return repository.getScribblesByDate(date.millis).flowOn(coroutineDispatcher)
    }
}