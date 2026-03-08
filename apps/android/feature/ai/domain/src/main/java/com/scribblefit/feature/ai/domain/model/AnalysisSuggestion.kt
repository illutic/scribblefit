package com.scribblefit.feature.ai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisSuggestion(
    val text: String,
    val emoji: String,
    val type: SuggestionType,
    val timestamp: Long
) {
    val fullText: String get() = "$text $emoji"
}

@Serializable
enum class SuggestionType { RECOVERY, PATTERN, MILESTONE, REST }
