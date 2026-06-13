package com.scribblefit.feature.exercises.domain

import com.scribblefit.core.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    suspend fun addExercise(scribbleId: Long, exercise: Exercise): Long
    suspend fun updateExercise(exercise: Exercise)
    suspend fun deleteExercise(exerciseId: Long)
    suspend fun getExerciseById(exerciseId: Long): Exercise?
    suspend fun getExercisesInRange(startDate: Long, endDate: Long): List<Exercise>
    suspend fun getExercisesForScribble(scribbleId: Long): List<Exercise>
    suspend fun getExercisesByName(exerciseName: String): List<Exercise>
    fun getExercisesByNameFlow(exerciseName: String): Flow<List<Exercise>>
}
