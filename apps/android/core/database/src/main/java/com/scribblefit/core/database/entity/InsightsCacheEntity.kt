package com.scribblefit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Insights_Cache")
data class InsightsCacheEntity(
    @PrimaryKey val key: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "display_text") val displayText: String,
    @ColumnInfo(name = "text") val text: String
)
