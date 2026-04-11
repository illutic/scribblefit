package com.scribblefit.core.model

enum class InsightType {
    SUMMARY,
    TREND,
    ADVICE
}

data class AIInsight(
    val insightType: InsightType,
    val text: String
)
