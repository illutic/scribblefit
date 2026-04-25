package com.scribblefit.feature.settings.data

import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.feature.settings.domain.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val database: ScribbleFitDatabase,
    private val json: Json,
    private val coroutineDispatcher: CoroutineDispatcher
) : SettingsRepository {

    override suspend fun clearAllUserData() {
        database.clearAllData()
    }

    override suspend fun exportUserData(): Flow<String> =
        database.scribbleDao()
            .getAllScribblesWithExercises()
            .flowOn(coroutineDispatcher)
            .map { scribbles ->
                val exportData = scribbles.map { scribble ->
                    ExportScribble(
                        createdAt = scribble.scribble.createdAt,
                        exercises = scribble.exercises.map { exercise ->
                            ExportExercise(
                                name = exercise.exercise.name,
                                sets = exercise.sets.map {
                                    ExportSet(
                                        setNumber = it.setNumber,
                                        reps = it.reps,
                                        weight = it.weight,
                                        rpe = it.rpe,
                                        notes = it.notes
                                    )
                                }
                            )
                        }
                    )
                }
                json.encodeToString(UserDataExport.serializer(), UserDataExport(exportData))
            }
}
