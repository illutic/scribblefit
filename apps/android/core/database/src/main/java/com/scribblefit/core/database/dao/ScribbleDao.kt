package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.scribblefit.core.database.entity.scribble.ScribbleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScribbleDao {
    @Insert
    suspend fun insertScribble(scribble: ScribbleEntity): Long

    @Update
    suspend fun updateScribble(scribble: ScribbleEntity)

    @Query("SELECT * FROM scribbles WHERE scribbleId = :id")
    fun getScribbleById(id: Long): Flow<ScribbleEntity>

    @Query("SELECT * FROM scribbles WHERE status = :status")
    fun getScribblesByStatus(status: String): Flow<List<ScribbleEntity>>
}
