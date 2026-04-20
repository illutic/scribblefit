package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

    @Query("UPDATE workout_exercise SET workoutId = :workoutId WHERE workoutExerciseId = :id")
    suspend fun updateWorkoutId(id: Long, workoutId: Long)

    @Update
    suspend fun updateWorkoutSet(workoutSet: WorkoutSet)

    @Query("UPDATE workout_set SET reps = :reps WHERE setId = :setId")
    suspend fun updateSetReps(setId: Long, reps: Int)

    @Query("UPDATE workout_set SET weight = :weight WHERE setId = :setId")
    suspend fun updateSetWeight(setId: Long, weight: Float?)

    @Query("SELECT * FROM workout_set WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    fun getSetsForWorkoutExercise(workoutExerciseId: Long): Flow<List<WorkoutSet>>
}
