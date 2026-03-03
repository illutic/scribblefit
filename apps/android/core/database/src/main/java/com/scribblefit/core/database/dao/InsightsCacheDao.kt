package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.scribblefit.core.database.model.InsightsCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InsightsCacheDao {
    @Upsert
    suspend fun upsertInsight(insight: InsightsCacheEntity)

    @Delete
    suspend fun deleteInsight(insight: InsightsCacheEntity)

    @Query("SELECT * FROM Insights_Cache WHERE `key` = :key")
    fun getInsightByKey(key: String): Flow<InsightsCacheEntity?>

    @Query("DELETE FROM Insights_Cache")
    suspend fun deleteAll()
}
