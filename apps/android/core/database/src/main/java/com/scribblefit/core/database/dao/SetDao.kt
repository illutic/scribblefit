package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.scribblefit.core.database.model.SetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    @Upsert
    suspend fun upsertSet(set: SetEntity)

    @Upsert
    suspend fun upsertSets(sets: List<SetEntity>)

    @Delete
    suspend fun deleteSet(set: SetEntity)

    @Query("SELECT * FROM Sets WHERE id = :id")
    fun getSetById(id: String): Flow<SetEntity?>

    @Query("SELECT * FROM Sets WHERE workout_id = :workoutId")
    fun getSetsForWorkout(workoutId: String): Flow<List<SetEntity>>

    @Query("DELETE FROM Sets")
    suspend fun deleteAll()
}
