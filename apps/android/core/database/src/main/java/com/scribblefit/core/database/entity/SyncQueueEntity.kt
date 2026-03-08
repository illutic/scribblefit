package com.scribblefit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Sync_Queue")
data class SyncQueueEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "raw_text") val rawText: String = "",
    @ColumnInfo(name = "status") val status: EntitySyncStatus,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "parsed_json") val parsedJson: String? = null
)

enum class EntitySyncStatus { PENDING, PROCESSING, COMPLETED, FAILED }
