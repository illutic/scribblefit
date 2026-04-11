package com.scribblefit.feature.ai.data.entity

import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.InsightType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class InsightTypeDto {
    @SerialName("summary") SUMMARY,
    @SerialName("trend") TREND,
    @SerialName("advice") ADVICE
}

@Serializable
internal data class AIInsightDto(
    val insightType: InsightTypeDto,
    val text: String
)

internal fun InsightTypeDto.toDomain(): InsightType = when (this) {
    InsightTypeDto.SUMMARY -> InsightType.SUMMARY
    InsightTypeDto.TREND -> InsightType.TREND
    InsightTypeDto.ADVICE -> InsightType.ADVICE
}

internal fun AIInsightDto.toDomain(): AIInsight = AIInsight(
    insightType = insightType.toDomain(),
    text = text
)
