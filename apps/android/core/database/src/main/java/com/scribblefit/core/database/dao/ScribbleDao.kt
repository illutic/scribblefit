package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.scribblefit.core.database.entity.scribble.ScribbleEntity
import com.scribblefit.core.database.entity.scribble.ScribbleWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface ScribbleDao {
    @Insert
    suspend fun insertScribble(scribble: ScribbleEntity): Long

    @Update
    suspend fun updateScribble(scribble: ScribbleEntity)

    @Query("DELETE FROM scribbles WHERE scribbleId = :id")
    suspend fun deleteScribble(id: Long)

    @Transaction
    @Query("SELECT * FROM scribbles WHERE scribbleId = :id")
    fun getScribbleById(id: Long): Flow<ScribbleWithExercises>

    @Transaction
    @Query("SELECT * FROM scribbles WHERE status = :status AND createdAt = :date")
    fun getScribblesByStatusAndDate(status: String, date: Long): Flow<List<ScribbleWithExercises>>

    @Transaction
    @Query("SELECT * FROM scribbles WHERE createdAt >= :date AND createdAt < :date + (24 * 60 * 60 * 1000)")
    fun getAllScribblesByDate(date: Long): Flow<List<ScribbleWithExercises>>

    @Transaction
    @Query("SELECT * FROM scribbles WHERE createdAt >= :startDate AND createdAt <= :endDate")
    fun getScribblesInRange(startDate: Long, endDate: Long): Flow<List<ScribbleWithExercises>>

    @Transaction
    @Query("SELECT * FROM scribbles")
    fun getAllScribbles(): Flow<List<ScribbleWithExercises>>
}
