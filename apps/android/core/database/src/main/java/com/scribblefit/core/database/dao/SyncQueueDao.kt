package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scribblefit.core.database.entity.EntitySyncStatus
import com.scribblefit.core.database.entity.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM Sync_Queue ORDER BY created_at ASC")
    fun observeAll(): Flow<List<SyncQueueEntity>>

    @Query("SELECT * FROM Sync_Queue WHERE status = 'PENDING' ORDER BY created_at ASC")
    suspend fun getPending(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SyncQueueEntity)

    @Query("UPDATE Sync_Queue SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: EntitySyncStatus)

    @Query("UPDATE Sync_Queue SET status = :status, parsed_json = :jsonData WHERE id = :id")
    suspend fun updateParsedResult(id: String, status: EntitySyncStatus, jsonData: String)

    @Query("DELETE FROM Sync_Queue WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM Sync_Queue")
    suspend fun deleteAll()
}
