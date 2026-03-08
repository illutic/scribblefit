package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scribblefit.core.database.entity.WorkoutLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {
    @Query("SELECT * FROM Workout_Logs ORDER BY date DESC")
    fun observeAll(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM Workout_Logs ORDER BY date DESC")
    suspend fun getAll(): List<WorkoutLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: WorkoutLogEntity)

    @Query("DELETE FROM Workout_Logs")
    suspend fun deleteAll()
}
