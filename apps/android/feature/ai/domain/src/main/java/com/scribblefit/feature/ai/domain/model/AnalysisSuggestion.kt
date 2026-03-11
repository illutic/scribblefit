package com.scribblefit.feature.ai.domain.model

data class AnalysisSuggestion(
    val text: String,
    val displayText: String,
    val type: SuggestionType,
    val timestamp: Long
)

enum class SuggestionType { RECOVERY, PATTERN, MILESTONE, REST }
