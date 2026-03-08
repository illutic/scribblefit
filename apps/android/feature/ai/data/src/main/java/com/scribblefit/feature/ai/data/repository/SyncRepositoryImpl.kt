package com.scribblefit.feature.ai.data.repository

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.scribblefit.core.database.dao.ExerciseDictionaryDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.SyncQueueDao
import com.scribblefit.core.database.dao.WorkoutLogDao
import com.scribblefit.core.database.model.SyncQueueEntity
import com.scribblefit.feature.ai.data.worker.SyncWorker
import com.scribblefit.feature.ai.domain.engine.SyncRepository
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SyncItem
import com.scribblefit.feature.ai.domain.model.SyncStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import com.scribblefit.core.database.model.SyncStatus as EntitySyncStatus

@Singleton
class SyncRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val syncQueueDao: SyncQueueDao,
    private val json: Json,
) : SyncRepository {

    internal var workManagerProvider: () -> WorkManager = { WorkManager.getInstance(context) }

    override fun getPendingSyncItems(): Flow<List<SyncItem>> {
        return syncQueueDao.getSyncItemsByStatus(EntitySyncStatus.PENDING).map { entities ->
            entities.map { it.toDomain(json) }
        }
    }

    override fun getAllSyncItems(): Flow<List<SyncItem>> {
        return syncQueueDao.getAllSyncItems().map { entities ->
            entities.map { it.toDomain(json) }
        }
    }

    override suspend fun updateSyncStatus(id: String, status: SyncStatus) {
        syncQueueDao.updateStatus(id, status.toEntity())
    }

    override suspend fun saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) {
        val jsonString = json.encodeToString(workout)
        syncQueueDao.updateParsedResult(syncItemId, EntitySyncStatus.COMPLETED, jsonString)
    }

    override suspend fun enqueueScribble(id: String, rawText: String) {
        val syncItem = SyncQueueEntity(
            id = id,
            type = "SCRIBBLE",
            rawText = rawText,
            status = EntitySyncStatus.PENDING,
            createdAt = System.currentTimeMillis()
        )
        syncQueueDao.upsertSyncItem(syncItem)
        triggerImmediateSync()
    }

    override suspend fun saveFeedItem(id: String, type: String, jsonData: String, status: SyncStatus) {
        val entity = SyncQueueEntity(
            id = id,
            type = type,
            parsedJson = jsonData,
            status = status.toEntity(),
            createdAt = System.currentTimeMillis(),
            rawText = ""
        )
        syncQueueDao.upsertSyncItem(entity)
    }

    override suspend fun deleteSyncItem(id: String) {
        syncQueueDao.deleteById(id)
    }

    private fun triggerImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                Duration.ofMinutes(1)
            )
            .build()

        workManagerProvider().enqueue(syncRequest)
    }
}

private fun SyncQueueEntity.toDomain(json: Json): SyncItem {
    val parsedWorkout = parsedJson?.let {
        runCatching { json.decodeFromString<ParsedWorkout>(it) }.getOrNull()
    }
    return SyncItem(
        id = id,
        type = type,
        rawText = rawText,
        status = status.toDomain(),
        createdAt = createdAt,
        jsonData = parsedJson,
        parsedResult = parsedWorkout
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
