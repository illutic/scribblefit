package com.scribblefit.feature.canvas.data.repository

import com.scribblefit.core.database.dao.CanvasFeedDao
import com.scribblefit.core.database.model.CanvasFeedEntity
import com.scribblefit.feature.ai.domain.engine.SyncRepository
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SuggestionType
import com.scribblefit.feature.ai.domain.model.SyncStatus
import com.scribblefit.feature.canvas.data.mapper.*
import com.scribblefit.feature.canvas.domain.model.*
import com.scribblefit.feature.canvas.domain.repository.CanvasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CanvasRepositoryImpl @Inject constructor(
    private val canvasFeedDao: CanvasFeedDao,
    private val syncRepository: SyncRepository,
    private val json: Json
) : CanvasRepository {

    override fun getFeed(): Flow<List<FeedItem>> {
        return combine(
            canvasFeedDao.getFeed(),
            syncRepository.getAllSyncItems()
        ) { feedEntities, syncItems ->
            val feedItems = feedEntities.mapNotNull { entity ->
                when (entity.type) {
                    "PROMPT" -> {
                        val dto = json.decodeFromString<FeedItemDto.Prompt>(entity.jsonData)
                        FeedItem.Prompt(
                            dto.id,
                            dto.timestamp,
                            dto.text,
                            dto.emoji,
                            SuggestionType.valueOf(dto.type)
                        )
                    }

                    "CONFIRMATION" -> {
                        val dto = json.decodeFromString<FeedItemDto.Confirmation>(entity.jsonData)
                        FeedItem.Confirmation(
                            dto.id,
                            dto.timestamp,
                            ParsedWorkout("2024-05-20", null, emptyList()),
                            dto.scribbleId
                        )
                    }

                    "INSIGHT" -> {
                        val dto = json.decodeFromString<FeedItemDto.Insight>(entity.jsonData)
                        FeedItem.Insight(dto.id, dto.timestamp, dto.text, dto.emoji)
                    }

                    else -> null
                }
            }

            val syncFeedItems = syncItems.map { item ->
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
                        rawText = item.rawText,
                        status = when (item.status) {
                            SyncStatus.PENDING -> ScribbleStatus.PENDING
                            SyncStatus.PROCESSING -> ScribbleStatus.PROCESSING
                            SyncStatus.FAILED -> ScribbleStatus.FAILED
                            SyncStatus.COMPLETED -> ScribbleStatus.COMPLETED
                        }
                    )
                }
            }

            (feedItems + syncFeedItems).sortedBy { it.timestamp }
        }
    }

    override suspend fun addScribble(rawText: String) {
        syncRepository.enqueueScribble(rawText, UUID.randomUUID().toString())
    }

    override suspend fun retryScribble(id: String) {
        syncRepository.updateSyncStatus(id, SyncStatus.PENDING)
    }

    override suspend fun addConfirmation(item: FeedItem.Confirmation) {
        val dto = FeedItemDto.Confirmation(item.id, item.timestamp, item.scribbleId)
        val entity =
            CanvasFeedEntity(item.id, "CONFIRMATION", json.encodeToString(dto), item.timestamp)
        canvasFeedDao.upsertFeedItem(entity)
    }

    override suspend fun addInsight(item: FeedItem.Insight) {
        val dto = FeedItemDto.Insight(item.id, item.timestamp, item.text, item.emoji)
        val entity = CanvasFeedEntity(item.id, "INSIGHT", json.encodeToString(dto), item.timestamp)
        canvasFeedDao.upsertFeedItem(entity)
    }

    override suspend fun removeFeedItem(id: String) {
        canvasFeedDao.deleteFeedItemById(id)
        syncRepository.deleteSyncItem(id)
    }

    override suspend fun clearFeed() {
        canvasFeedDao.clearFeed()
    }
}
