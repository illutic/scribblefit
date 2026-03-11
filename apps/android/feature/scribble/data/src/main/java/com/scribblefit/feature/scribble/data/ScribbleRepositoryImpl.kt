package com.scribblefit.feature.scribble.data

import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.dao.ScribbleDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.core.database.entity.EntitySyncStatus
import com.scribblefit.core.database.entity.ScribbleEntity
import com.scribblefit.feature.ai.domain.model.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.model.ParsingStatus
import com.scribblefit.feature.scribble.domain.Scribble
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.scribble.domain.SyncStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ScribbleRepositoryImpl(
    private val scribbleDao: ScribbleDao,
    private val setDao: SetDao,
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    coroutineDispatcher: CoroutineDispatcher,
) : ScribbleRepository,
    CoroutineScope by CoroutineScope(coroutineDispatcher + CoroutineName("SyncRepository")) {
    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun getAllScribbles(): Flow<List<Scribble>> = scribbleDao
        .observeAll()
        .map { scribbles -> scribbles.map { getSyncItemFromScribbleEntity(it) } }
        .distinctUntilChanged()

    override fun getPendingScribbles(): Flow<List<Scribble.Raw>> = scribbleDao
        .observePendingScribbles()
        .map { scribbles -> scribbles.map { it.toDomain() } }
        .distinctUntilChanged()

    override suspend fun updateSyncStatus(id: String, status: SyncStatus) = runCatching {
        if (status is SyncStatus.Completed) {
            handleParsedResult(status.parsedResult)
            return@runCatching
        }
        scribbleDao.updateStatus(id, status.toEntity())
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun enqueueScribble(rawText: String) {
        val entity = ScribbleEntity(
            id = Uuid.random().toString(),
            rawText = rawText,
            status = EntitySyncStatus.PENDING,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        scribbleDao.insert(entity)
    }

    private suspend fun getSyncItemFromScribbleEntity(scribbleEntity: ScribbleEntity): Scribble {
        val exerciseId = scribbleEntity.exerciseId
        return if (exerciseId != null) {
            val exerciseEntity =
                exerciseDao.getById(exerciseId) ?: error("Unable to find exercise $exerciseId")
            val setEntities = setDao.getSetsForWorkout(exerciseId)

            Scribble.Parsed(
                id = scribbleEntity.id,
                createdAt = scribbleEntity.createdAt,
                value = exerciseEntity.toDomain(setEntities.map { it.toDomain() })
            )
        } else {
            scribbleEntity.toDomain()
        }
    }

    private suspend fun handleParsedResult(result: ParsedWorkoutResult) {
        val workout = result.workout

        when (result.status) {
            ParsingStatus.SUCCESS -> Unit
            ParsingStatus.PARTIAL_SUCCESS -> {
                logger.warn("Received a partial success status for $result")
            }

            ParsingStatus.FAILURE -> {
                logger.error("Failed to parse $result")
                return
            }
        }

        if (workout == null) {
            logger.warn("Got a null workout, which means the parsing failed for some reason.")
            return
        }

        val workoutEntity = workout.toEntity()

        val exercisesWithSets = workout.exercises.map { exercise ->
            val exerciseEntity = exercise.toEntity()
            val exerciseSets = exercise.sets.map { set ->
                set.toEntity(
                    exerciseId = exerciseEntity.id,
                    workoutId = workoutEntity.id
                )
            }
            exerciseEntity to exerciseSets
        }

        workoutDao.upsert(workoutEntity)

        exercisesWithSets.forEach { (exercise, sets) ->
            exerciseDao.insertExerciseIfAbsent(exercise)
            setDao.upsertAll(sets)
        }
    }
}
