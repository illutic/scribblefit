package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.flow.Flow

class GetScribblesForDateUseCase(
    private val repository: ScribbleRepository
) {
    operator fun invoke(date: CurrentDate): Flow<List<Scribble>> {
        return repository.getScribblesByDate(date.startOfDayInMillis)
    }
}