package com.scribblefit.feature.scribble.domain

import com.scribblefit.core.model.Scribble
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing scribbles.
 */
interface ScribbleRepository {
    suspend fun saveRawScribble(text: String): Long
    suspend fun updateScribbleWithParsedData(scribbleId: Long, parsedJson: String)
    suspend fun markScribbleCompleted(scribbleId: Long, workoutExerciseId: Long)
    suspend fun markScribbleFailed(scribbleId: Long)
    fun getScribble(scribbleId: Long): Flow<Scribble>
    fun getPendingScribbles(): Flow<List<Scribble>>
}
