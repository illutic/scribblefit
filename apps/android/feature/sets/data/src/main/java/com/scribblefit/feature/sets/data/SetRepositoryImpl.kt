package com.scribblefit.feature.sets.data

import com.scribblefit.core.database.dao.WorkoutExerciseDao
import com.scribblefit.core.database.mapper.toDomain
import com.scribblefit.core.database.mapper.toEntity
import com.scribblefit.core.model.Set
import com.scribblefit.feature.sets.domain.SetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class SetRepositoryImpl(
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val coroutineDispatcher: CoroutineDispatcher
) : SetRepository {

    override suspend fun addSet(workoutExerciseId: Long, set: Set): Long =
        withContext(coroutineDispatcher) {
            workoutExerciseDao.insertWorkoutSet(set.toEntity(workoutExerciseId))
        }

    override suspend fun updateSetReps(setId: Long, reps: Int) =
        withContext(coroutineDispatcher) {
            workoutExerciseDao.updateSetReps(setId, reps)
        }

    override suspend fun updateSetWeight(setId: Long, weight: Float?) =
        withContext(coroutineDispatcher) {
            workoutExerciseDao.updateSetWeight(setId, weight)
        }

    override suspend fun deleteSet(setId: Long) =
        withContext(coroutineDispatcher) {
            workoutExerciseDao.deleteWorkoutSet(setId)
        }

    override fun getSetsForExercise(workoutExerciseId: Long): Flow<List<Set>> =
        workoutExerciseDao
            .getSetsForWorkoutExercise(workoutExerciseId)
            .flowOn(coroutineDispatcher)
            .map { entities -> entities.map { it.toDomain() } }
}
