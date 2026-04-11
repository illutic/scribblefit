package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.scribblefit.core.database.entity.exercise.WorkoutExercise
import com.scribblefit.core.database.entity.set.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutExerciseDao {
    @Insert
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long

    @Insert
    suspend fun insertWorkoutExercises(workoutExercises: List<WorkoutExercise>): List<Long>

    @Insert
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet): Long

    @Insert
    suspend fun insertWorkoutSets(workoutSets: List<WorkoutSet>)

    @Query("DELETE FROM workout_exercise WHERE workoutExerciseId = :id")
    suspend fun deleteWorkoutExercise(id: Long)

    @Query("DELETE FROM workout_set WHERE setId = :setId")
    suspend fun deleteWorkoutSet(setId: Long)

    @Query("SELECT * FROM workout_set WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    fun getSetsForWorkoutExercise(workoutExerciseId: Long): Flow<List<WorkoutSet>>
}
