package com.scribblefit.feature.ledger.domain.repository

import com.scribblefit.feature.workout.domain.Exercise
import com.scribblefit.feature.workout.domain.Workout
import kotlinx.coroutines.flow.Flow

interface LedgerRepository {
    fun getWorkoutHistory(): Flow<List<Workout>>
    suspend fun logWorkout(exercise: Exercise)
}
