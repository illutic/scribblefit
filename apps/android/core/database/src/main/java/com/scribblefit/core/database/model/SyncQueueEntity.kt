package com.scribblefit.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SyncStatus {
    PENDING, PROCESSING, FAILED, COMPLETED
}

@Entity(tableName = "Sync_Queue")
data class SyncQueueEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "raw_text")
    val rawText: String,
    val status: SyncStatus,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
