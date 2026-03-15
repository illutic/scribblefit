package com.scribblefit.feature.exercises.data

import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.dao.WorkoutExerciseDao
import com.scribblefit.core.database.entity.exercise.WorkoutExercise
import com.scribblefit.core.database.mapper.toDomain
import com.scribblefit.core.database.mapper.toEntity
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExerciseRepositoryImpl(
    private val exerciseDao: ExerciseDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val coroutineDispatcher: CoroutineDispatcher
) : ExerciseRepository {

    override suspend fun addExercise(exercise: Exercise): Long = withContext(coroutineDispatcher) {
        exerciseDao.insertExercise(exercise.toEntity())
    }

    override suspend fun updateExercise(exercise: Exercise) = withContext(coroutineDispatcher) {
        exerciseDao.updateExercise(exercise.toEntity())
    }

    override suspend fun deleteExercise(exerciseId: Long) = withContext(coroutineDispatcher) {
        exerciseDao.deleteExercise(exerciseId)
    }

    override fun searchExercises(query: String): Flow<List<Exercise>> =
        exerciseDao
            .getExercisesByName(query)
            .flowOn(coroutineDispatcher)
            .map { entities -> entities.map { it.toDomain() } }

    override fun getExercisesByMuscleGroup(group: String): Flow<List<Exercise>> =
        exerciseDao
            .getExercisesByMuscleGroup(group)
            .flowOn(coroutineDispatcher)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun addExerciseToWorkout(
        workoutId: Long,
        exerciseId: Long
    ): Long = withContext(coroutineDispatcher) {
        workoutExerciseDao.insertWorkoutExercise(
            WorkoutExercise(
                workoutId = workoutId,
                exerciseId = exerciseId
            )
        )
    }

    override fun getWorkoutExerciseDetails(workoutExerciseId: Long): Flow<Exercise> =
        workoutExerciseDao
            .getWorkoutExerciseWithDetails(workoutExerciseId)
            .flowOn(coroutineDispatcher)
            .map { it.toDomain() }
}
