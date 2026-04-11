package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.ZoneOffset

class GetScribblesForDateUseCase(
    private val repository: ScribbleRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(date: LocalDate): Flow<List<Scribble>> {
        val timestamp = date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        return repository.getScribblesByDate(timestamp).flatMapLatest { scribbles ->
            if (scribbles.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(scribbles.map { scribble ->
                    repository.getScribbleWithExercises(scribble.id)
                }) { it.toList() }
            }
        }
    }
}