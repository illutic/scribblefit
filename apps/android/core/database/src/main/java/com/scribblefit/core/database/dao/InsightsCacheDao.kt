package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scribblefit.core.database.model.InsightsCacheEntity

@Dao
interface InsightsCacheDao {
    @Query("SELECT * FROM Insights_Cache WHERE `key` = :key")
    suspend fun getCacheEntry(key: String): InsightsCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheEntry(entity: InsightsCacheEntity)

    @Query("DELETE FROM Insights_Cache WHERE `key` = :key")
    suspend fun deleteCacheEntry(key: String)

    @Query("DELETE FROM Insights_Cache")
    suspend fun clearAll()
}
