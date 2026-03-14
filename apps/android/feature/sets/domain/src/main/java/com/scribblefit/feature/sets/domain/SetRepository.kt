package com.scribblefit.feature.sets.domain

import com.scribblefit.core.model.Set
import kotlinx.coroutines.flow.Flow

interface SetRepository {
    suspend fun addSet(workoutExerciseId: Long, set: Set): Long
    fun getSetsForExercise(workoutExerciseId: Long): Flow<List<Set>>
}
