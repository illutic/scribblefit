package com.scribblefit.feature.exercises.data

import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.entity.exercise.toDomain
import com.scribblefit.core.database.entity.exercise.toEntity
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class ExerciseRepositoryImpl(
    private val exerciseDao: ExerciseDao,
    private val coroutineDispatcher: CoroutineDispatcher
) : ExerciseRepository {
    override suspend fun addExercise(
        scribbleId: Long,
        exercise: Exercise
    ): Long = withContext(coroutineDispatcher) {
        val exerciseEntity = exercise.toEntity(scribbleId)

        val exerciseId = exerciseDao.insertExercise(exerciseEntity)

        return@withContext exerciseId
    }

    override suspend fun updateExercise(exercise: Exercise) = withContext(coroutineDispatcher) {
        val exerciseEntity = exerciseDao.getExerciseById(exercise.id).firstOrNull()
            ?: error("Exercise not found with id: ${exercise.id}")
        val newExerciseEntity = exercise.toEntity(exerciseEntity.exercise.exerciseId)

        exerciseDao.updateExercise(newExerciseEntity)
    }

    override suspend fun deleteExercise(exerciseId: Long) = withContext(coroutineDispatcher) {
        exerciseDao.deleteExercise(exerciseId)
    }

    override suspend fun getExerciseById(exerciseId: Long): Exercise? =
        withContext(coroutineDispatcher) {
            val exerciseEntity =
                exerciseDao.getExerciseById(exerciseId).firstOrNull() ?: return@withContext null
            return@withContext exerciseEntity.toDomain()
        }

    override suspend fun getExercisesInRange(
        startDate: Long,
        endDate: Long
    ): List<Exercise> = withContext(coroutineDispatcher) {
        val exerciseEntities =
            exerciseDao.getExercisesWithSetsInRange(startDate, endDate).firstOrNull()
                ?: return@withContext emptyList()
        return@withContext exerciseEntities.map { it.toDomain() }
    }

    override suspend fun getExercisesForScribble(scribbleId: Long): List<Exercise> =
        withContext(coroutineDispatcher) {
            val exerciseEntities =
                exerciseDao.getExercisesByScribbleId(scribbleId).firstOrNull()
                    ?: return@withContext emptyList()
            return@withContext exerciseEntities.map { it.toDomain() }
        }

    override suspend fun getExercisesByName(exerciseName: String): List<Exercise> =
        withContext(coroutineDispatcher) {
            val exerciseEntities =
                exerciseDao.getExercisesWithSetsForName(exerciseName).firstOrNull()
                    ?: return@withContext emptyList()
            return@withContext exerciseEntities.map { it.toDomain() }
        }

    override fun getExercisesByNameFlow(exerciseName: String): Flow<List<Exercise>> =
        exerciseDao.getExercisesWithSetsForName(exerciseName).map { entities ->
            entities.map { it.toDomain() }
        }
}
