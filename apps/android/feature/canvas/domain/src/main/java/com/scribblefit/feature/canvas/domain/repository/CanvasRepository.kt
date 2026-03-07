package com.scribblefit.feature.canvas.domain.repository

import com.scribblefit.feature.canvas.domain.model.FeedItem
import kotlinx.coroutines.flow.Flow

interface CanvasRepository {
    /**
     * Observes the conversational feed for the current active session.
     */
    fun getFeed(): Flow<List<FeedItem>>

    /**
     * Adds a raw user entry to the persistent sync queue and feed.
     */
    suspend fun addScribble(rawText: String)

    /**
     * Retries a failed parsing attempt.
     */
    suspend fun retryScribble(id: String)

    /**
     * Adds a structured confirmation card to the feed.
     */
    suspend fun addConfirmation(item: FeedItem.Confirmation)

    /**
     * Adds a micro-insight bubble to the feed.
     */
    suspend fun addInsight(item: FeedItem.Insight)

    /**
     * Removes a specific item from the conversational feed.
     */
    suspend fun removeFeedItem(id: String)

    /**
     * Clears the current canvas feed (e.g., after 4 hours of inactivity).
     */
    suspend fun clearFeed()
}
