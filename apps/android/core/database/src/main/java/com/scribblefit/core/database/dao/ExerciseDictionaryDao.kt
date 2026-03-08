package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.scribblefit.core.database.model.ExerciseDictionaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDictionaryDao {
    @Upsert
    suspend fun upsertExercise(exercise: ExerciseDictionaryEntity)

    @Upsert
    suspend fun upsertExercises(exercises: List<ExerciseDictionaryEntity>)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseDictionaryEntity)

    @Query("SELECT * FROM Exercise_Dictionary WHERE id = :id")
    fun getExerciseById(id: String): Flow<ExerciseDictionaryEntity?>

    @Query("SELECT * FROM Exercise_Dictionary ORDER BY canonical_name ASC")
    fun getAllExercises(): Flow<List<ExerciseDictionaryEntity>>

    @Query(
        "SELECT * FROM Exercise_Dictionary WHERE canonical_name LIKE '%' " +
                "|| :query || '%' OR aliases LIKE '%' || :query || '%'"
    )
    fun searchExercises(query: String): Flow<List<ExerciseDictionaryEntity>>

    @Query("DELETE FROM Exercise_Dictionary")
    suspend fun deleteAll()
}
