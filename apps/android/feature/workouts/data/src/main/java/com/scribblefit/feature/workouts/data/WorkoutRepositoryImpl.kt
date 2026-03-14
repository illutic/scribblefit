package com.scribblefit.feature.workouts.data

import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.core.database.mapper.toDomain
import com.scribblefit.core.database.mapper.toEntity
import com.scribblefit.core.model.Workout
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class WorkoutRepositoryImpl(
    private val workoutDao: WorkoutDao,
    private val coroutineDispatcher: CoroutineDispatcher
) : WorkoutRepository {

    override suspend fun saveWorkout(workout: Workout): Long = withContext(coroutineDispatcher) {
        workoutDao.insertWorkout(workout.toEntity())
    }

    override fun getWorkoutById(id: Long): Flow<Workout> =
        workoutDao
            .getWorkoutWithAllDetails(id)
            .flowOn(coroutineDispatcher)
            .map { it.toDomain() }


    override fun getWorkoutsInDateRange(startDate: Long, endDate: Long): Flow<List<Workout>> =
        workoutDao
            .getWorkoutsInDateRange(startDate, endDate)
            .flowOn(coroutineDispatcher)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun deleteWorkout(workoutId: Long) = withContext(coroutineDispatcher) {
        workoutDao.deleteWorkout(workoutId)
    }
}
