package com.scribblefit.feature.workouts.domain

import com.scribblefit.core.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    suspend fun saveWorkout(workout: Workout): Long
    fun getWorkoutByDate(date: Long): Flow<Workout?>
    fun getWorkoutById(workoutId: Long): Flow<Workout?>
    fun getWorkoutsInRange(startDate: Long, endDate: Long): Flow<List<Workout>>
}
