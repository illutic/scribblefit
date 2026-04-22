package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId

class GetScribblesForDateUseCase(
    private val repository: ScribbleRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<Scribble>> {
        val timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return repository.getScribblesByDate(timestamp)
    }
}