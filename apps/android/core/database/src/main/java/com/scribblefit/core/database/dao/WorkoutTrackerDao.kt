package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.scribblefit.core.database.entity.exercise.WorkoutExercise
import com.scribblefit.core.database.entity.set.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutTrackerDao {
    @Insert
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long

    @Insert
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet): Long

    /**
     * Gets all sets for a specific exercise entry in a workout.
     */
    @Query("SELECT * FROM workout_set WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    fun getSetsForWorkoutExercise(workoutExerciseId: Long): Flow<List<WorkoutSet>>
}
