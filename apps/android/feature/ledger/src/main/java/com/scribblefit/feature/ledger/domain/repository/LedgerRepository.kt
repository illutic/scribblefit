package com.scribblefit.feature.ledger.domain.repository

import com.scribblefit.feature.ledger.domain.model.WorkoutHistory
import kotlinx.coroutines.flow.Flow

interface LedgerRepository {
    fun getWorkoutHistory(): Flow<List<WorkoutHistory>>
    suspend fun logWorkout(workout: WorkoutHistory)
}
