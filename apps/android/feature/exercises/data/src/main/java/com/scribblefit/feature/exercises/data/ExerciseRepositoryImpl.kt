package com.scribblefit.feature.exercises.data

import androidx.room.withTransaction
import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.core.database.entity.exercise.toDomain
import com.scribblefit.core.database.entity.exercise.toEntity
import com.scribblefit.core.database.entity.set.SetEntity
import com.scribblefit.core.database.entity.set.toEntity
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class ExerciseRepositoryImpl(
    private val database: ScribbleFitDatabase,
    private val coroutineDispatcher: CoroutineDispatcher
) : ExerciseRepository {
    private val exerciseDao = database.exerciseDao()
    private val setDao = database.setDao()
    override suspend fun addExercise(
        scribbleId: Long,
        exercise: Exercise
    ): Long = withContext(coroutineDispatcher) {
        val exerciseEntity = exercise.toEntity(scribbleId)

        val exerciseId = exerciseDao.insertExercise(exerciseEntity)

        return@withContext exerciseId
    }

    override suspend fun addExercises(
        scribbleId: Long,
        exercises: List<Exercise>
    ): List<Long> = withContext(coroutineDispatcher) {
        val exerciseEntities = exercises.map { it.toEntity(scribbleId) }
        return@withContext exerciseDao.insertWorkoutExercises(exerciseEntities)
    }

    override suspend fun addExercisesWithSets(
        scribbleId: Long,
        exercises: List<Exercise>
    ) = withContext(coroutineDispatcher) {
        database.withTransaction {
            val exerciseEntities = exercises.map { it.toEntity(scribbleId) }
            val addedExerciseIds = exerciseDao.insertWorkoutExercises(exerciseEntities)

            val allSets = mutableListOf<SetEntity>()
            exercises.forEachIndexed { index, exercise ->
                val newExerciseId = addedExerciseIds[index]
                allSets.addAll(exercise.sets.map { it.toEntity(newExerciseId) })
            }
            if (allSets.isNotEmpty()) {
                setDao.insertWorkoutSets(allSets)
            }
        }
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
            val exerciseEntities = exerciseDao.getExercisesByScribbleIdSync(scribbleId)
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
