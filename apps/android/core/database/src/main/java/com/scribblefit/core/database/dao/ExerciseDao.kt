package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.scribblefit.core.database.entity.exercise.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Query("SELECT exerciseId FROM exercise WHERE name = :name")
    suspend fun getExerciseIdByName(name: String): Long?

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Query("DELETE FROM exercise WHERE exerciseId = :exerciseId")
    suspend fun deleteExercise(exerciseId: Long)

    @Query("SELECT * FROM exercise WHERE name LIKE '%' || :searchQuery || '%'")
    fun getExercisesByName(searchQuery: String): Flow<List<Exercise>>

    @Query("SELECT * FROM exercise WHERE muscleGroup = :muscleGroup")
    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<Exercise>>
}
