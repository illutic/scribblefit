package com.scribblefit.feature.workouts.domain

import com.scribblefit.core.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    suspend fun saveWorkout(workout: Workout): Long
    fun getWorkoutById(id: Long): Flow<Workout>
    fun getWorkoutsInDateRange(startDate: Long, endDate: Long): Flow<List<Workout>>
    suspend fun deleteWorkout(workoutId: Long)
}
