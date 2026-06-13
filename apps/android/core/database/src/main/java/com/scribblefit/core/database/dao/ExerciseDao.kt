package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.scribblefit.core.database.entity.exercise.ExerciseEntity
import com.scribblefit.core.database.entity.exercise.ExerciseWithSets
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)

    @Query("DELETE FROM exercises WHERE exerciseId = :exerciseId")
    suspend fun deleteExercise(exerciseId: Long)

    @Transaction
    @Query("SELECT * FROM exercises WHERE exerciseId = :exerciseId")
    fun getExerciseById(exerciseId: Long): Flow<ExerciseWithSets?>

    @Transaction
    @Query("SELECT * FROM exercises WHERE scribbleId = :scribbleId")
    fun getExercisesByScribbleId(scribbleId: Long): Flow<List<ExerciseWithSets>>

    @Transaction
    @Query("SELECT * FROM exercises WHERE scribbleId = :scribbleId")
    suspend fun getExercisesByScribbleIdSync(scribbleId: Long): List<ExerciseWithSets>

    @Transaction
    @Query("SELECT * FROM exercises WHERE exerciseId = :exerciseId")
    fun getExerciseWithSets(exerciseId: Long): Flow<ExerciseWithSets?>

    @Transaction
    @Query("SELECT * FROM exercises WHERE createdAt >= :startDate AND createdAt <= :endDate")
    fun getExercisesWithSetsInRange(startDate: Long, endDate: Long): Flow<List<ExerciseWithSets>>

    @Transaction
    @Query("SELECT * FROM exercises WHERE createdAt = :date")
    fun getExercisesWithSetsForDate(date: Long): Flow<List<ExerciseWithSets>>

    @Transaction
    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :name || '%'")
    fun getExercisesWithSetsForName(name: String): Flow<List<ExerciseWithSets>>

    @Query("DELETE FROM exercises")
    suspend fun clearAllExercises()
}
