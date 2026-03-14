package com.scribblefit.feature.exercises.domain

import com.scribblefit.core.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    suspend fun addExercise(exercise: Exercise): Long
    fun searchExercises(query: String): Flow<List<Exercise>>
    fun getExercisesByMuscleGroup(group: String): Flow<List<Exercise>>
}
