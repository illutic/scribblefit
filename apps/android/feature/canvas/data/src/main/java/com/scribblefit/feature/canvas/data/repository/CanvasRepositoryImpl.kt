package com.scribblefit.feature.canvas.data.repository

import com.scribblefit.feature.ai.domain.engine.SyncRepository
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SyncStatus
import com.scribblefit.feature.canvas.domain.model.FeedItem
import com.scribblefit.feature.canvas.domain.model.ScribbleStatus
import com.scribblefit.feature.canvas.domain.repository.CanvasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CanvasRepositoryImpl @Inject constructor(
    private val syncRepository: SyncRepository,
    private val json: Json
) : CanvasRepository {

    override fun getFeed(): Flow<List<FeedItem>> =
        syncRepository.getAllSyncItems().map { items ->
            items.mapNotNull { item ->
                when (item.type) {
                    "SCRIBBLE" -> {
                        if (item.status == SyncStatus.COMPLETED && item.jsonData != null) {
                            val workout = runCatching {
                                json.decodeFromString<ParsedWorkout>(item.jsonData!!)
                            }.getOrNull()
                            if (workout != null) {
                                FeedItem.Confirmation(
                                    id = item.id,
                                    timestamp = item.createdAt,
                                    workout = workout,
                                    scribbleId = item.id
                                )
                            } else {
                                FeedItem.Scribble(
                                    id = item.id,
                                    timestamp = item.createdAt,
                                    rawText = item.rawText ?: "",
                                    status = item.status.toScribbleStatus()
                                )
                            }
                        } else {
                            FeedItem.Scribble(
                                id = item.id,
                                timestamp = item.createdAt,
                                rawText = item.rawText ?: "",
                                status = item.status.toScribbleStatus()
                            )
                        }
                    }
                    else -> null
                }
            }.sortedBy { it.timestamp() }
        }

    override suspend fun addScribble(rawText: String) {
        syncRepository.enqueueScribble(UUID.randomUUID().toString(), rawText)
    }

    override suspend fun retryScribble(id: String) {
        syncRepository.updateSyncStatus(id, SyncStatus.PENDING)
        syncRepository.syncWorkouts()
    }

    override suspend fun addConfirmation(item: FeedItem.Confirmation) {
        val encoded = json.encodeToString(item.workout)
        syncRepository.saveFeedItem(item.id, "CONFIRMATION", encoded, SyncStatus.COMPLETED)
    }

    override suspend fun addInsight(item: FeedItem.Insight) {
        syncRepository.saveFeedItem(item.id, "INSIGHT", "", SyncStatus.COMPLETED)
    }

    override suspend fun removeFeedItem(id: String) {
        syncRepository.deleteSyncItem(id)
    }

    override suspend fun clearFeed() {
        // Clears handled at database level
    }

    private fun SyncStatus.toScribbleStatus(): ScribbleStatus = when (this) {
        SyncStatus.PENDING -> ScribbleStatus.PENDING
        SyncStatus.PROCESSING -> ScribbleStatus.PROCESSING
        SyncStatus.FAILED -> ScribbleStatus.FAILED
        SyncStatus.COMPLETED -> ScribbleStatus.COMPLETED
    }

    private fun FeedItem.timestamp(): Long = when (this) {
        is FeedItem.Prompt -> timestamp
        is FeedItem.Scribble -> timestamp
        is FeedItem.Confirmation -> timestamp
        is FeedItem.Insight -> timestamp
    }
}
