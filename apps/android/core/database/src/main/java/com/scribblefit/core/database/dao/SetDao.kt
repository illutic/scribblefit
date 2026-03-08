package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scribblefit.core.database.entity.SetEntity

@Dao
interface SetDao {
    @Query("SELECT * FROM Sets WHERE workout_id = :workoutId")
    suspend fun getSetsForWorkout(workoutId: String): List<SetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(sets: List<SetEntity>)

    @Query("DELETE FROM Sets")
    suspend fun deleteAll()
}
