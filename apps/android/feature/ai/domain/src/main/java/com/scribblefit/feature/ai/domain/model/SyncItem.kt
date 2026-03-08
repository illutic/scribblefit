package com.scribblefit.feature.ai.domain.model

data class SyncItem(
    val id: String,
    val type: String,
    val rawText: String?,
    val status: SyncStatus,
    val createdAt: Long,
    val jsonData: String?,
    val parsedResult: ParsedWorkout? = null
)

enum class SyncStatus { PENDING, PROCESSING, COMPLETED, FAILED }
