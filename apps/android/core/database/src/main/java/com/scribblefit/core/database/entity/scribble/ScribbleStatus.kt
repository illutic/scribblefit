package com.scribblefit.core.database.entity.scribble

/**
 * Tracks the lifecycle of a scribble input.
 */
enum class ScribbleStatus {
    RAW,       // Stored, waiting for LLM processing
    PARSED,    // LLM returned data, waiting for user confirmation
    COMPLETED, // User confirmed, linked to actual workout data
    FAILED     // LLM failed to parse
}
