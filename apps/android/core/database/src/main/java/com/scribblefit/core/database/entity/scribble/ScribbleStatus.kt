package com.scribblefit.core.database.entity.scribble

/**
 * Tracks the lifecycle of a scribble input.
 */
enum class ScribbleStatus {
    PENDING,   // Stored, waiting for LLM processing
    PARSING,   // LLM is currently processing the scribble
    SUCCESS,   // LLM returned data, waiting for user confirmation
    COMPLETED, // User confirmed, linked to actual workout data
    FAILED     // LLM failed to parse
}
