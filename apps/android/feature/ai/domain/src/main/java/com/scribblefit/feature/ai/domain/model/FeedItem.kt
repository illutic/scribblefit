package com.scribblefit.feature.ai.domain.model

/**
 * Represents an item in the Intelligent Canvas feed.
 */
sealed class FeedItem {
    abstract val id: String
    abstract val timestamp: Long

    /**
     * An AI-generated prompt or suggestion.
     */
    data class Prompt(
        override val id: String,
        override val timestamp: Long,
        val text: String,
        val emoji: String,
        val type: SuggestionType
    ) : FeedItem()

    /**
     * A raw user entry (text or voice).
     */
    data class Scribble(
        override val id: String,
        override val timestamp: Long,
        val rawText: String,
        val status: ScribbleStatus
    ) : FeedItem()

    /**
     * A parsed, structured workout ready for confirmation.
     */
    data class Confirmation(
        override val id: String,
        override val timestamp: Long,
        val workout: ParsedWorkout,
        val scribbleId: String
    ) : FeedItem()

    /**
     * A micro-insight (e.g., "New PR!").
     */
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
