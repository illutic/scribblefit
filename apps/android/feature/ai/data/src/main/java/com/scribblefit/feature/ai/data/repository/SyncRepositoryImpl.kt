package com.scribblefit.feature.ai.data.repository

import com.scribblefit.core.database.dao.ExerciseDictionaryDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.SyncQueueDao
import com.scribblefit.core.database.dao.WorkoutLogDao
import com.scribblefit.core.database.model.SetEntity
import com.scribblefit.core.database.model.WorkoutLogEntity
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SyncItem
import com.scribblefit.feature.ai.domain.model.SyncStatus
import com.scribblefit.feature.ai.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import com.scribblefit.core.database.model.SyncStatus as EntitySyncStatus

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val workoutLogDao: WorkoutLogDao,
    private val setDao: SetDao,
    private val exerciseDictionaryDao: ExerciseDictionaryDao
) : SyncRepository {

    override fun getPendingSyncItems(): Flow<List<SyncItem>> {
        return syncQueueDao.getSyncItemsByStatus(EntitySyncStatus.PENDING).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateSyncStatus(id: String, status: SyncStatus) {
        syncQueueDao.updateStatus(id, status.toEntity())
    }

    override suspend fun saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) {
        val workoutId = UUID.randomUUID().toString()
        
        val workoutDate = runCatching {
            Instant.parse(workout.date).toEpochMilli()
        }.getOrDefault(System.currentTimeMillis())

        val workoutLog = WorkoutLogEntity(
            id = workoutId,
            date = workoutDate,
            location = workout.location,
            totalVolume = 0.0 // To be calculated or provided by AI
        )
        
        val sets = workout.exercises.flatMap { exercise ->
            // Fuzzy search for the canonical ID from our local dictionary
            val exerciseId = exerciseDictionaryDao.searchExercises(exercise.canonicalName)
                .first()
                .firstOrNull()?.id ?: exercise.canonicalName // Fallback to name as ID

            exercise.sets.map { set ->
                SetEntity(
                    id = UUID.randomUUID().toString(),
                    workoutId = workoutId,
                    exerciseId = exerciseId,
                    weight = set.weight,
                    reps = set.reps,
                    rpe = set.rpe,
                    notes = set.notes
                )
            }
        }
        
        workoutLogDao.upsertWorkoutLog(workoutLog)
        setDao.upsertSets(sets)
        
        // Mark sync item as completed
        updateSyncStatus(syncItemId, SyncStatus.COMPLETED)
    }
}

private fun com.scribblefit.core.database.model.SyncQueueEntity.toDomain(): SyncItem {
    return SyncItem(
        id = id,
        rawText = rawText,
        status = status.toDomain(),
        createdAt = createdAt
    )
}

private fun EntitySyncStatus.toDomain(): SyncStatus = when (this) {
    EntitySyncStatus.PENDING -> SyncStatus.PENDING
    EntitySyncStatus.PROCESSING -> SyncStatus.PROCESSING
    EntitySyncStatus.COMPLETED -> SyncStatus.COMPLETED
    EntitySyncStatus.FAILED -> SyncStatus.FAILED
}

private fun SyncStatus.toEntity(): EntitySyncStatus = when (this) {
    SyncStatus.PENDING -> EntitySyncStatus.PENDING
    SyncStatus.PROCESSING -> EntitySyncStatus.PROCESSING
    SyncStatus.COMPLETED -> EntitySyncStatus.COMPLETED
    SyncStatus.FAILED -> EntitySyncStatus.FAILED
}
