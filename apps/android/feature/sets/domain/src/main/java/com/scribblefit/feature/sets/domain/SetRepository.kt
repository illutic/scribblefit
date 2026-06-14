package com.scribblefit.feature.sets.domain

import com.scribblefit.core.model.Set

interface SetRepository {
    suspend fun addSet(exerciseId: Long, set: Set): Long
    suspend fun addSets(exerciseId: Long, sets: List<Set>): List<Long>
    suspend fun updateSetReps(setId: Long, reps: Int)
    suspend fun updateSetWeight(setId: Long, weight: Float?)
    suspend fun deleteSet(setId: Long)
}
