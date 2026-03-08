package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scribblefit.core.database.entity.ExerciseDictionaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDictionaryDao {
    @Query("SELECT * FROM Exercise_Dictionary ORDER BY canonical_name ASC")
    fun observeAll(): Flow<List<ExerciseDictionaryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercisesIfAbsent(exercises: List<ExerciseDictionaryEntity>)

    @Query("DELETE FROM Exercise_Dictionary")
    suspend fun deleteAll()
}
