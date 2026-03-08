package com.scribblefit.feature.ai.data.repository

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.scribblefit.core.database.dao.SyncQueueDao
import com.scribblefit.core.database.entity.EntitySyncStatus
import com.scribblefit.core.database.entity.SyncQueueEntity
import com.scribblefit.feature.ai.data.worker.SyncWorker
import com.scribblefit.feature.ai.domain.engine.SyncRepository
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SyncItem
import com.scribblefit.feature.ai.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val workManager: WorkManager,
    private val json: Json
) : SyncRepository {

    override fun getPendingSyncItems(): Flow<List<SyncItem>> =
        syncQueueDao.observeAll().map { entities ->
            entities.filter { it.status == EntitySyncStatus.PENDING }.map { it.toDomain() }
        }

    override fun getAllSyncItems(): Flow<List<SyncItem>> =
        syncQueueDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun updateSyncStatus(id: String, status: SyncStatus) {
        syncQueueDao.updateStatus(id, status.toEntity())
    }

    override suspend fun saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) {
        val jsonString = json.encodeToString(workout)
        syncQueueDao.updateParsedResult(syncItemId, EntitySyncStatus.COMPLETED, jsonString)
    }

    override suspend fun enqueueScribble(id: String, rawText: String) {
        val entity = SyncQueueEntity(
            id = id,
            type = "SCRIBBLE",
            rawText = rawText,
            status = EntitySyncStatus.PENDING,
            createdAt = System.currentTimeMillis()
        )
        syncQueueDao.insert(entity)
    }

    override suspend fun saveFeedItem(id: String, type: String, jsonData: String, status: SyncStatus) {
        val entity = SyncQueueEntity(
            id = id,
            type = type,
            status = status.toEntity(),
            createdAt = System.currentTimeMillis(),
            parsedJson = jsonData
        )
        syncQueueDao.insert(entity)
    }

    override suspend fun deleteSyncItem(id: String) {
        syncQueueDao.deleteById(id)
    }

    override suspend fun syncWorkouts() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueue(request)
    }

    private fun SyncQueueEntity.toDomain(): SyncItem {
        val parsedResult = if (status == EntitySyncStatus.COMPLETED && parsedJson != null) {
            runCatching { json.decodeFromString<ParsedWorkout>(parsedJson!!) }.getOrNull()
        } else null
        return SyncItem(
            id = id,
            type = type,
            rawText = rawText.ifEmpty { null },
            status = status.toDomain(),
            createdAt = createdAt,
            jsonData = parsedJson,
            parsedResult = parsedResult
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
}
