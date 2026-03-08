package com.scribblefit.feature.ledger.data.repository

import com.scribblefit.core.database.dao.ExerciseDictionaryDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.WorkoutLogDao
import com.scribblefit.core.database.model.ExerciseDictionaryEntity
import com.scribblefit.core.database.model.SetEntity
import com.scribblefit.core.database.model.WorkoutLogEntity
import com.scribblefit.feature.ledger.domain.model.ExerciseHistory
import com.scribblefit.feature.ledger.domain.model.SetHistory
import com.scribblefit.feature.ledger.domain.model.WorkoutHistory
import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LedgerRepositoryImpl @Inject constructor(
    private val workoutLogDao: WorkoutLogDao,
    private val setDao: SetDao,
    private val exerciseDictionaryDao: ExerciseDictionaryDao
) : LedgerRepository {

    override fun getWorkoutHistory(): Flow<List<WorkoutHistory>> {
        return workoutLogDao.getAllWorkoutLogs().map { logs ->
            logs.map { log ->
                val sets = setDao.getSetsForWorkout(log.id).first()

                val exercises = sets.groupBy { it.exerciseId }.map { (exerciseId, setEntities) ->
                    val exerciseName =
                        exerciseDictionaryDao.getExerciseById(exerciseId).first()?.canonicalName
                            ?: exerciseId

                    ExerciseHistory(
                        canonicalName = exerciseName,
                        sets = setEntities.map {
                            SetHistory(it.weight, it.reps, it.rpe, it.notes)
                        }
                    )
                }

                WorkoutHistory(
                    id = log.id,
                    date = log.date,
                    location = log.location,
                    totalVolume = log.totalVolume ?: 0.0,
                    exercises = exercises
                )
            }
        }
    }

    override suspend fun logWorkout(workout: WorkoutHistory) {
        workoutLogDao.upsertWorkoutLog(
            WorkoutLogEntity(
                id = workout.id,
                date = workout.date,
                location = workout.location,
                totalVolume = workout.totalVolume
            )
        )

        val exerciseEntities = workout.exercises.map { exercise ->
            ExerciseDictionaryEntity(
                id = exercise.canonicalName,
                canonicalName = exercise.canonicalName,
                muscleGroup = "",
                aliases = emptyList()
            )
        }
        exerciseDictionaryDao.insertExercisesIfAbsent(exerciseEntities)

        val setEntities = workout.exercises.flatMap { exercise ->
            exercise.sets.map { set ->
                SetEntity(
                    id = UUID.randomUUID().toString(),
                    workoutId = workout.id,
                    exerciseId = exercise.canonicalName,
                    weight = set.weight,
                    reps = set.reps,
                    rpe = set.rpe,
                    notes = set.notes
                )
            }
        }
        setDao.upsertSets(setEntities)
    }
}
