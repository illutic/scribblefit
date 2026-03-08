package com.scribblefit.feature.ledger.data.repository

import com.scribblefit.core.database.dao.ExerciseDictionaryDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.WorkoutLogDao
import com.scribblefit.core.database.entity.ExerciseDictionaryEntity
import com.scribblefit.core.database.entity.SetEntity
import com.scribblefit.core.database.entity.WorkoutLogEntity
import com.scribblefit.feature.ledger.domain.model.ExerciseHistory
import com.scribblefit.feature.ledger.domain.model.SetHistory
import com.scribblefit.feature.ledger.domain.model.WorkoutHistory
import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import kotlinx.coroutines.flow.Flow
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

    override fun getWorkoutHistory(): Flow<List<WorkoutHistory>> =
        workoutLogDao.observeAll().map { logs ->
            logs.map { log ->
                val sets = setDao.getSetsForWorkout(log.id)
                val grouped = sets.groupBy { it.exerciseId }
                val exercises = grouped.map { (exerciseId, exerciseSets) ->
                    ExerciseHistory(
                        canonicalName = exerciseId,
                        sets = exerciseSets.map { s ->
                            SetHistory(weight = s.weight, reps = s.reps, rpe = s.rpe, notes = s.notes)
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

    override suspend fun logWorkout(workout: WorkoutHistory) {
        val exerciseEntities = workout.exercises.map { exercise ->
            ExerciseDictionaryEntity(
                id = exercise.canonicalName,
                canonicalName = exercise.canonicalName,
                muscleGroup = "",
                aliases = emptyList()
            )
        }
        exerciseDictionaryDao.insertExercisesIfAbsent(exerciseEntities)

        workoutLogDao.upsert(
            WorkoutLogEntity(
                id = workout.id,
                date = workout.date,
                location = workout.location,
                totalVolume = workout.totalVolume
            )
        )

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
        setDao.upsertAll(setEntities)
    }
}
