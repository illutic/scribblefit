package com.scribblefit.feature.exercises.domain

import com.scribblefit.core.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    suspend fun addExercise(exercise: Exercise): Long
    suspend fun updateExercise(exercise: Exercise)
    suspend fun deleteExercise(exerciseId: Long)
    suspend fun addExerciseToWorkout(workoutId: Long, exerciseId: Long): Long
    fun searchExercises(query: String): Flow<List<Exercise>>
    fun getExercisesByMuscleGroup(group: String): Flow<List<Exercise>>
    fun getWorkoutExerciseDetails(workoutExerciseId: Long): Flow<Exercise>
}
