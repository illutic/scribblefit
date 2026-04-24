package com.scribblefit.feature.scribble.domain

import com.scribblefit.core.model.Scribble
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for scribbles.
 */
interface ScribbleRepository {
    suspend fun insertScribble(scribble: Scribble): Long
    suspend fun updateScribble(scribble: Scribble)
    suspend fun deleteScribble(scribbleId: Long)
    fun getScribble(scribbleId: Long): Flow<Scribble>
    fun getScribblesByDate(date: Long): Flow<List<Scribble>>
}
