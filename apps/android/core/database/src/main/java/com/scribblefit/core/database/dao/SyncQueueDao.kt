package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.scribblefit.core.database.model.SyncQueueEntity
import com.scribblefit.core.database.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {
    @Upsert
    suspend fun upsertSyncItem(syncItem: SyncQueueEntity)

    @Delete
    suspend fun deleteSyncItem(syncItem: SyncQueueEntity)

    @Query("SELECT * FROM Sync_Queue WHERE id = :id")
    fun getSyncItemById(id: String): Flow<SyncQueueEntity?>

    @Query("SELECT * FROM Sync_Queue WHERE status = :status ORDER BY created_at ASC")
    fun getSyncItemsByStatus(status: SyncStatus): Flow<List<SyncQueueEntity>>

    @Query("SELECT * FROM Sync_Queue ORDER BY created_at ASC")
    fun getAllSyncItems(): Flow<List<SyncQueueEntity>>

    @Query("UPDATE Sync_Queue SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: SyncStatus)

    @Query("UPDATE Sync_Queue SET status = :status, parsed_json = :parsedJson WHERE id = :id")
    suspend fun updateParsedResult(id: String, status: SyncStatus, parsedJson: String?)

    @Query("DELETE FROM Sync_Queue WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM Sync_Queue")
    suspend fun deleteAll()
}
