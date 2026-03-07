package com.scribblefit.feature.canvas.data.repository

import com.scribblefit.core.database.dao.CanvasFeedDao
import com.scribblefit.core.database.dao.SyncQueueDao
import com.scribblefit.core.database.model.CanvasFeedEntity
import com.scribblefit.core.database.model.SyncQueueEntity
import com.scribblefit.core.database.model.SyncStatus
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SuggestionType
import com.scribblefit.feature.canvas.data.mapper.*
import com.scribblefit.feature.canvas.domain.model.*
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
                    "CONFIRMATION" -> {
                        val dto = json.decodeFromString<FeedItemDto.Confirmation>(entity.jsonData)
                        FeedItem.Confirmation(dto.id, dto.timestamp, ParsedWorkout("2024-05-20", null, emptyList()), dto.scribbleId)
                    }
                    "INSIGHT" -> {
                        val dto = json.decodeFromString<FeedItemDto.Insight>(entity.jsonData)
                        FeedItem.Insight(dto.id, dto.timestamp, dto.text, dto.emoji)
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

    override suspend fun addConfirmation(item: FeedItem.Confirmation) {
        val dto = FeedItemDto.Confirmation(item.id, item.timestamp, item.scribbleId)
        val entity = CanvasFeedEntity(item.id, "CONFIRMATION", json.encodeToString(dto), item.timestamp)
        canvasFeedDao.upsertFeedItem(entity)
    }

    override suspend fun addInsight(item: FeedItem.Insight) {
        val dto = FeedItemDto.Insight(item.id, item.timestamp, item.text, item.emoji)
        val entity = CanvasFeedEntity(item.id, "INSIGHT", json.encodeToString(dto), item.timestamp)
        canvasFeedDao.upsertFeedItem(entity)
    }

    override suspend fun removeFeedItem(id: String) {
        canvasFeedDao.deleteFeedItemById(id)
    }

    override suspend fun clearFeed() {
        canvasFeedDao.clearFeed()
    }
}
