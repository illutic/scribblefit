package com.scribblefit.feature.canvas.domain.repository

import com.scribblefit.feature.canvas.domain.model.FeedItem
import kotlinx.coroutines.flow.Flow

interface CanvasRepository {
    fun getFeed(): Flow<List<FeedItem>>
    suspend fun addScribble(rawText: String)
    suspend fun retryScribble(id: String)
    suspend fun addConfirmation(item: FeedItem.Confirmation)
    suspend fun addInsight(item: FeedItem.Insight)
    suspend fun removeFeedItem(id: String)
    suspend fun clearFeed()
}
