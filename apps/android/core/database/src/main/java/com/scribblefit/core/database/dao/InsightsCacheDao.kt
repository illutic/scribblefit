package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scribblefit.core.database.entity.InsightsCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InsightsCacheDao {
    @Query("SELECT * FROM Insights_Cache WHERE `key` = :key LIMIT 1")
    fun getByKey(key: String): Flow<InsightsCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cache: InsightsCacheEntity)

    @Query("DELETE FROM Insights_Cache")
    suspend fun deleteAll()
}
