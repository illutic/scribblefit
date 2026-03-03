package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.scribblefit.core.database.model.WorkoutLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {
    @Upsert
    suspend fun upsertWorkoutLog(workoutLog: WorkoutLogEntity)

    @Upsert
    suspend fun upsertWorkoutLogs(workoutLogs: List<WorkoutLogEntity>)

    @Delete
    suspend fun deleteWorkoutLog(workoutLog: WorkoutLogEntity)

    @Query("SELECT * FROM Workout_Logs WHERE id = :id")
    fun getWorkoutLogById(id: String): Flow<WorkoutLogEntity?>

    @Query("SELECT * FROM Workout_Logs ORDER BY date DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLogEntity>>

    @Query("DELETE FROM Workout_Logs")
    suspend fun deleteAll()
}
