package com.scribblefit.feature.canvas.domain.model

import com.scribblefit.core.ai.model.*

sealed class FeedItem {
    abstract val id: String
    abstract val timestamp: Long

    data class Prompt(
        override val id: String,
        override val timestamp: Long,
        val text: String,
        val emoji: String,
        val type: SuggestionType
    ) : FeedItem()

    data class Scribble(
        override val id: String,
        override val timestamp: Long,
        val rawText: String,
        val status: ScribbleStatus
    ) : FeedItem()

    data class Confirmation(
        override val id: String,
        override val timestamp: Long,
        val workout: ParsedWorkout,
        val scribbleId: String
    ) : FeedItem()

    data class Insight(
        override val id: String,
        override val timestamp: Long,
        val text: String,
        val emoji: String
    ) : FeedItem()
}

enum class ScribbleStatus {
    PENDING, PROCESSING, FAILED, COMPLETED
}
