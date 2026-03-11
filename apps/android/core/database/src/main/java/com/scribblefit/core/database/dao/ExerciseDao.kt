package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scribblefit.core.database.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM Exercise ORDER BY canonical_name ASC")
    fun observeAll(): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExerciseIfAbsent(exercise: ExerciseEntity)

    @Query("SELECT * FROM Exercise WHERE id = :id")
    suspend fun getById(id: String): ExerciseEntity?

    @Query("DELETE FROM Exercise")
    suspend fun deleteAll()
}
