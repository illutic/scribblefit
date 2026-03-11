package com.scribblefit.feature.ledger.data.repository

import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.feature.workout.domain.Exercise
import com.scribblefit.feature.workout.domain.Set
import com.scribblefit.feature.workout.domain.Workout
import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LedgerRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val setDao: SetDao,
    private val exerciseDao: ExerciseDao
) : LedgerRepository {

    override fun getWorkoutHistory(): Flow<List<Workout>> =
        workoutDao.observeAll().map { logs ->
            logs.map { log ->
                val sets = setDao.getSetsForWorkout(log.id)
                val grouped = sets.groupBy { it.exerciseId }
                val exercises = grouped.map { (exerciseId, exerciseSets) ->
                    Exercise(
                        canonicalName = exerciseId,
                        muscleGroup = "",
                        sets = exerciseSets.map { s ->
                            Set(weight = s.weight, reps = s.reps, rpe = s.rpe, notes = s.notes)
                        }
                    )
                }
                Workout(
                    date = log.date,
                    exercises = exercises
                )
            }
        }

    override suspend fun logWorkout(exercise: Exercise) {

//        val exerciseEntities = workout.exercises.map { exercise ->
//            val entity = ExerciseEntity(
//                id = exercise.canonicalName,
//                canonicalName = exercise.canonicalName,
//                muscleGroup = "",
//                aliases = emptyList()
//            )
//        }
//
//        exerciseDao.insertExerciseIfAbsent(exerciseEntities)
//
//        workoutDao.upsert(
//            WorkoutEntity(
//                id = workout.id,
//                date = workout.date,
//                location = workout.location,
//                totalVolume = workout.totalVolume
//            )
//        )
//
//        val setEntities = workout.exercises.flatMap { exercise ->
//            exercise.sets.map { set ->
//                SetEntity(
//                    id = UUID.randomUUID().toString(),
//                    workoutId = workout.id,
//                    exerciseId = exercise.canonicalName,
//                    weight = set.weight,
//                    reps = set.reps,
//                    rpe = set.rpe,
//                    notes = set.notes
//                )
//            }
//        }
//        setDao.upsertAll(setEntities)
    }
}
