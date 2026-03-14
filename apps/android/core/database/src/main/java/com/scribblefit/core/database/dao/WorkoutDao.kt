package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.scribblefit.core.database.entity.workout.Workout
import com.scribblefit.core.database.entity.workout.WorkoutWithAllDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Query("DELETE FROM workout WHERE workoutId = :workoutId")
    suspend fun deleteWorkout(workoutId: Long)

    /**
     * Retrieves a workout with its full hierarchy (Exercises -> Sets).
     * Uses @Transaction because Room performs multiple queries to resolve @Relation.
     */
    @Transaction
    @Query("SELECT * FROM workout WHERE workoutId = :workoutId")
    fun getWorkoutWithAllDetails(workoutId: Long): Flow<WorkoutWithAllDetails>

    /**
     * Returns a list of workouts within a specific timeframe.
     * Standard history queries filter out draft workouts.
     */
    @Query("SELECT * FROM workout WHERE isDraft = 0 AND workoutDate BETWEEN :startDate AND :endDate ORDER BY workoutDate DESC")
    fun getWorkoutsInDateRange(startDate: Long, endDate: Long): Flow<List<Workout>>
}
