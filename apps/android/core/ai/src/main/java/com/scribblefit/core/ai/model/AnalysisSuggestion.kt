package com.scribblefit.core.ai.model

data class AnalysisSuggestion(
    val text: String,
    val emoji: String,
    val type: SuggestionType,
    val timestamp: Long
) {
    val fullText: String get() = "$text $emoji"
}

enum class SuggestionType {
    RECOVERY, PATTERN, MILESTONE, REST
}
