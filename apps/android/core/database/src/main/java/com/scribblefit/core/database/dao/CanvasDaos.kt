package com.scribblefit.core.database.dao

import androidx.room.*
import com.scribblefit.core.database.model.CanvasFeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CanvasFeedDao {
    @Query("SELECT * FROM Canvas_Feed ORDER BY created_at ASC")
    fun getFeed(): Flow<List<CanvasFeedEntity>>

    @Upsert
    suspend fun upsertFeedItem(item: CanvasFeedEntity)

    @Delete
    suspend fun deleteFeedItem(item: CanvasFeedEntity)

    @Query("DELETE FROM Canvas_Feed")
    suspend fun clearFeed()
}

@Dao
interface ActiveSessionDao {
    @Query("SELECT * FROM Active_Session WHERE id = 'current_session'")
    fun getActiveSession(): Flow<com.scribblefit.core.database.model.ActiveSessionEntity?>

    @Upsert
    suspend fun upsertSession(session: com.scribblefit.core.database.model.ActiveSessionEntity)

    @Query("DELETE FROM Active_Session WHERE id = 'current_session'")
    suspend fun clearSession()
}
