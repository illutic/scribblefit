package com.scribblefit.feature.workouts.data

import com.scribblefit.core.database.dao.ExerciseStats
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.core.database.entity.set.WorkoutSet
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

    override suspend fun saveWorkoutWithDetails(workout: Workout): Long = withContext(coroutineDispatcher) {
        val workoutEntity = workout.toEntity()
        val exerciseEntities = workout.exercises.map { it.toEntity() }
        val setsPerExercise = workout.exercises.map { exercise ->
            exercise.sets.map { set ->
                WorkoutSet(
                    workoutExerciseId = 0,
                    setNumber = set.setNumber,
                    weight = set.weight,
                    reps = set.reps,
                    rpe = set.rpe,
                    notes = set.notes,
                )
            }
        }
        val exerciseStats = workout.exercises.map { exercise ->
            ExerciseStats(
                estimated1RM = exercise.estimated1RM,
                intensity = exercise.intensity,
                improvement = exercise.improvement,
            )
        }
        workoutDao.insertWorkoutWithDetails(workoutEntity, exerciseEntities, setsPerExercise, exerciseStats)
    }

    override suspend fun updateWorkout(workout: Workout): Unit = withContext(coroutineDispatcher) {
        val workoutEntity = workout.toEntity()
        val exerciseEntities = workout.exercises.map { it.toEntity() }
        val setsPerExercise = workout.exercises.map { exercise ->
            exercise.sets.map { set ->
                WorkoutSet(
                    workoutExerciseId = 0,
                    setNumber = set.setNumber,
                    weight = set.weight,
                    reps = set.reps,
                    rpe = set.rpe,
                    notes = set.notes,
                )
            }
        }
        val exerciseStats = workout.exercises.map { exercise ->
            ExerciseStats(
                estimated1RM = exercise.estimated1RM,
                intensity = exercise.intensity,
                improvement = exercise.improvement,
            )
        }
        workoutDao.updateWorkoutWithDetails(workoutEntity, exerciseEntities, setsPerExercise, exerciseStats)
    }

    override fun getWorkoutByDate(date: Long): Flow<Workout?> =
        workoutDao
            .getWorkoutByDate(date)
            .flowOn(coroutineDispatcher)
            .map { entity -> entity?.toDomain() }

    override fun getWorkoutById(workoutId: Long): Flow<Workout?> =
        workoutDao
            .getWorkoutWithAllDetails(workoutId)
            .flowOn(coroutineDispatcher)
            .map { it.toDomain() }

    override fun getWorkoutsInRange(startDate: Long, endDate: Long): Flow<List<Workout>> =
        workoutDao
            .getWorkoutsWithAllDetailsInRange(startDate, endDate)
            .flowOn(coroutineDispatcher)
            .map { list -> list.map { it.toDomain() } }

    override fun getWorkoutsWithExercise(exerciseName: String): Flow<List<Workout>> =
        workoutDao
            .getWorkoutsByExerciseName(exerciseName)
            .flowOn(coroutineDispatcher)
            .map { list -> list.map { it.toDomain() } }

}
