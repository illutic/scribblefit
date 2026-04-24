package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.scribblefit.core.database.entity.scribble.ScribbleEntity
import com.scribblefit.core.database.entity.scribble.ScribbleWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface ScribbleDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertScribble(scribbleEntity: ScribbleEntity): Long

    @Update
    suspend fun updateScribble(scribbleEntity: ScribbleEntity)

    @Query("DELETE FROM scribbles WHERE scribbleId = :scribbleId")
    suspend fun deleteScribble(scribbleId: Long)

    @Transaction
    @Query("SELECT * FROM scribbles WHERE scribbleId = :scribbleId")
    fun getScribbleWithExercises(scribbleId: Long): Flow<ScribbleWithExercises?>

    @Transaction
    @Query("SELECT * FROM scribbles WHERE createdAt = :date")
    fun getScribblesWithExercisesForDate(date: Long): Flow<List<ScribbleWithExercises>>

    @Transaction
    @Query("SELECT * FROM scribbles WHERE createdAt >= :startDate AND createdAt <= :endDate")
    fun getScribblesWithExercisesInRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<ScribbleWithExercises>>

    @Transaction
    @Query("SELECT * FROM scribbles")
    fun getAllScribblesWithExercises(): Flow<List<ScribbleWithExercises>>

    @Query("DELETE FROM scribbles")
    suspend fun clearAllScribbles()
}