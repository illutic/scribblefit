package com.scribblefit.feature.scribble.domain

import com.scribblefit.core.model.Scribble
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing scribbles.
 */
interface ScribbleRepository {
    suspend fun insertScribble(scribble: Scribble): Long
    suspend fun updateScribble(scribble: Scribble)
    suspend fun deleteScribble(scribbleId: Long)
    suspend fun clearScribbleExercises(scribbleId: Long)
    suspend fun addExerciseToScribble(scribbleId: Long, workoutExerciseId: Long): Long
    fun getScribble(scribbleId: Long): Flow<Scribble>
    fun getScribbleWithExercises(scribbleId: Long): Flow<Scribble>
    fun getPendingScribblesByDate(date: Long): Flow<List<Scribble>>
    fun getScribblesByDate(date: Long): Flow<List<Scribble>>
    fun getScribblesInRange(startDate: Long, endDate: Long): Flow<List<Scribble>>
}
