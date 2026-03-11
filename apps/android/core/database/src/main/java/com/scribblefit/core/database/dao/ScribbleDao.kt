package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scribblefit.core.database.entity.EntitySyncStatus
import com.scribblefit.core.database.entity.ScribbleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScribbleDao {
    @Query("SELECT * FROM Scribble ORDER BY created_at ASC")
    fun observeAll(): Flow<List<ScribbleEntity>>

    @Query("SELECT * FROM Scribble WHERE status = 'PENDING' ORDER BY created_at ASC")
    fun observePendingScribbles(): Flow<List<ScribbleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ScribbleEntity)

    @Query("UPDATE Scribble SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: EntitySyncStatus)

    @Query("DELETE FROM Scribble WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM Scribble")
    suspend fun deleteAll()
}
