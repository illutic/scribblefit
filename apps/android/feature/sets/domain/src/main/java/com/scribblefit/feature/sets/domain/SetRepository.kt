package com.scribblefit.feature.sets.domain

import com.scribblefit.core.model.Set
import kotlinx.coroutines.flow.Flow

interface SetRepository {
    suspend fun addSet(workoutExerciseId: Long, set: Set): Long
    suspend fun updateSetReps(setId: Long, reps: Int)
    suspend fun updateSetWeight(setId: Long, weight: Float)
    suspend fun deleteSet(setId: Long)
    fun getSetsForExercise(workoutExerciseId: Long): Flow<List<Set>>
}
