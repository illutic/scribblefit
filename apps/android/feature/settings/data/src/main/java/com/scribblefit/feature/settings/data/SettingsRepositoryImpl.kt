package com.scribblefit.feature.settings.data

import com.scribblefit.core.database.dao.ScribbleTrackerDao
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.feature.settings.domain.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val scribbleTrackerDao: ScribbleTrackerDao,
    private val workoutDao: WorkoutDao,
    private val json: Json
) : SettingsRepository {

    override suspend fun clearAllUserData() {
        workoutDao.clearAllUserData()
    }

    override suspend fun exportUserData(): Flow<String> {
        return combine(
            scribbleTrackerDao.getAllScribblesWithExercises(),
            workoutDao.getAllWorkoutsWithAllDetails()
        ) { scribbles, workouts ->
            val export = UserDataExport(
                scribbles = scribbles.map { s ->
                    ScribbleExport(
                        id = s.scribble.scribbleId,
                        rawText = s.scribble.rawText,
                        status = s.scribble.status,
                        createdAt = s.scribble.createdAt,
                        exercises = s.exercises.map { e ->
                            ScribbleExerciseExport(
                                exerciseName = e.exercise.name,
                                muscleGroup = e.exercise.muscleGroup,
                                sets = e.sets.map { s ->
                                    WorkoutSetExport(
                                        setNumber = s.setNumber,
                                        reps = s.reps,
                                        weight = s.weight,
                                        rpe = s.rpe,
                                        notes = s.notes
                                    )
                                }
                            )
                        }
                    )
                },
                workouts = workouts.map { w ->
                    WorkoutExport(
                        id = w.workout.workoutId,
                        date = w.workout.workoutDate,
                        notes = w.workout.notes,
                        exercises = w.exercises.map { we ->
                            WorkoutExerciseExport(
                                exerciseName = we.exercise.name,
                                muscleGroup = we.exercise.muscleGroup,
                                sets = we.sets.map { s ->
                                    WorkoutSetExport(
                                        setNumber = s.setNumber,
                                        reps = s.reps,
                                        weight = s.weight,
                                        rpe = s.rpe,
                                        notes = s.notes
                                    )
                                }
                            )
                        }
                    )
                }
            )
            json.encodeToString(export)
        }
    }
}
