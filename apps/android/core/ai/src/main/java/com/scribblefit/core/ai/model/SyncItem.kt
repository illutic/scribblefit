package com.scribblefit.core.ai.model

enum class SyncStatus {
    PENDING, PROCESSING, COMPLETED, FAILED
}

data class SyncItem(
    val id: String,
    val rawText: String,
    val status: SyncStatus,
    val createdAt: Long
)
