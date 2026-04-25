package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.scribblefit.core.database.entity.set.SetEntity

@Dao
interface SetDao {
    @Insert
    suspend fun insertSet(setEntity: SetEntity): Long

    @Update
    suspend fun updateSet(setEntity: SetEntity)

    @Query("SELECT * FROM sets WHERE setId = :setId")
    suspend fun getSetById(setId: Long): SetEntity?

    @Query("DELETE FROM sets WHERE setId = :setId")
    suspend fun deleteSet(setId: Long)

    @Query("DELETE FROM sets")
    suspend fun clearAllSets()
}