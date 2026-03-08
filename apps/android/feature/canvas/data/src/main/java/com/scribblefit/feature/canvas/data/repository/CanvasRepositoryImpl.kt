package com.scribblefit.feature.canvas.data.repository

import com.scribblefit.feature.ai.domain.engine.SyncRepository
import com.scribblefit.feature.ai.domain.model.SuggestionType
import com.scribblefit.feature.ai.domain.model.SyncStatus
import com.scribblefit.feature.canvas.data.mapper.*
import com.scribblefit.feature.canvas.domain.model.*
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

    override fun getFeed(): Flow<List<FeedItem>> {
        return syncRepository.getAllSyncItems().map { items ->
            items.mapNotNull { item ->
                when (item.type) {
                    "SCRIBBLE" -> {
                        if (item.status == SyncStatus.COMPLETED && item.parsedResult != null) {
                            FeedItem.Confirmation(
                                id = item.id,
                                timestamp = item.createdAt,
                                workout = item.parsedResult!!,
                                scribbleId = item.id
                            )
                        } else {
                            FeedItem.Scribble(
                                id = item.id,
                                timestamp = item.createdAt,
                                rawText = item.rawText ?: "",
                                status = when (item.status) {
                                    SyncStatus.PENDING -> ScribbleStatus.PENDING
                                    SyncStatus.PROCESSING -> ScribbleStatus.PROCESSING
                                    SyncStatus.FAILED -> ScribbleStatus.FAILED
                                    SyncStatus.COMPLETED -> ScribbleStatus.COMPLETED
                                }
                            )
                        }
                    }

                    "PROMPT" -> {
                        val dto = item.jsonData?.let {
                            runCatching { json.decodeFromString<FeedItemDto.Prompt>(it) }.getOrNull()
                        } ?: return@mapNotNull null
                        FeedItem.Prompt(
                            dto.id,
                            dto.timestamp,
                            dto.text,
                            dto.emoji,
                            SuggestionType.valueOf(dto.type)
                        )
                    }

                    "CONFIRMATION" -> {
                        // This handles manual confirmations not linked to a scribble
                        val dto = item.jsonData?.let {
                            runCatching { json.decodeFromString<FeedItemDto.Confirmation>(it) }.getOrNull()
                        } ?: return@mapNotNull null
                        
                        FeedItem.Confirmation(
                            dto.id,
                            dto.timestamp,
                            item.parsedResult ?: return@mapNotNull null,
                            dto.scribbleId
                        )
                    }

                    "INSIGHT" -> {
                        val dto = item.jsonData?.let {
                            runCatching { json.decodeFromString<FeedItemDto.Insight>(it) }.getOrNull()
                        } ?: return@mapNotNull null
                        FeedItem.Insight(dto.id, dto.timestamp, dto.text, dto.emoji)
                    }

                    else -> null
                }
            }.sortedBy { it.timestamp }
        }
    }

    override suspend fun addScribble(rawText: String) {
        syncRepository.enqueueScribble(UUID.randomUUID().toString(), rawText)
    }

    override suspend fun retryScribble(id: String) {
        syncRepository.updateSyncStatus(id, SyncStatus.PENDING)
    }

    override suspend fun addConfirmation(item: FeedItem.Confirmation) {
        val dto = FeedItemDto.Confirmation(item.id, item.timestamp, item.scribbleId)
        syncRepository.saveFeedItem(
            id = item.id,
            type = "CONFIRMATION",
            jsonData = json.encodeToString(dto),
        )
        syncRepository.saveParsedWorkout(item.id, item.workout)
    }

    override suspend fun addInsight(item: FeedItem.Insight) {
        val dto = FeedItemDto.Insight(item.id, item.timestamp, item.text, item.emoji)
        syncRepository.saveFeedItem(
            id = item.id,
            type = "INSIGHT",
            jsonData = json.encodeToString(dto)
        )
    }

    override suspend fun removeFeedItem(id: String) {
        syncRepository.deleteSyncItem(id)
    }

    override suspend fun clearFeed() {
        // This might need a new method in SyncRepository if we only want to clear "Feed" items
        // but for now deleteSyncItem is used.
    }
}
