package com.scribblefit.feature.canvas.data.repository

import com.scribblefit.core.database.dao.CanvasFeedDao
import com.scribblefit.core.database.dao.SyncQueueDao
import com.scribblefit.core.database.model.CanvasFeedEntity
import com.scribblefit.core.database.model.SyncQueueEntity
import com.scribblefit.core.database.model.SyncStatus
import com.scribblefit.feature.canvas.data.mapper.*
import com.scribblefit.feature.canvas.domain.model.*
import com.scribblefit.core.ai.model.*
import com.scribblefit.feature.canvas.domain.repository.CanvasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CanvasRepositoryImpl @Inject constructor(
    private val canvasFeedDao: CanvasFeedDao,
    private val syncQueueDao: SyncQueueDao,
    private val json: Json
) : CanvasRepository {

    override fun getFeed(): Flow<List<FeedItem>> {
        return canvasFeedDao.getFeed().map { entities ->
            entities.map { entity ->
                when (entity.type) {
                    "SCRIBBLE" -> {
                        val dto = json.decodeFromString<FeedItemDto.Scribble>(entity.jsonData)
                        FeedItem.Scribble(dto.id, dto.timestamp, dto.rawText, ScribbleStatus.valueOf(dto.status))
                    }
                    "PROMPT" -> {
                        val dto = json.decodeFromString<FeedItemDto.Prompt>(entity.jsonData)
                        FeedItem.Prompt(dto.id, dto.timestamp, dto.text, dto.emoji, SuggestionType.valueOf(dto.type))
                    }
                    else -> throw IllegalStateException("Unknown feed item type: ${entity.type}")
                }
            }
        }
    }

    override suspend fun addScribble(rawText: String) {
        val id = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        
        syncQueueDao.upsertSyncItem(SyncQueueEntity(id, rawText, SyncStatus.PENDING, timestamp))
        
        val scribbleDto = FeedItemDto.Scribble(id, timestamp, rawText, ScribbleStatus.PENDING.name)
        val entity = CanvasFeedEntity(id, "SCRIBBLE", json.encodeToString(scribbleDto), timestamp)
        canvasFeedDao.upsertFeedItem(entity)
    }

    override suspend fun retryScribble(id: String) {
        syncQueueDao.updateStatus(id, SyncStatus.PENDING)
    }

    override suspend fun addConfirmation(item: FeedItem.Confirmation) {}

    override suspend fun clearFeed() {
        canvasFeedDao.clearFeed()
    }
}
