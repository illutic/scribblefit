package com.scribblefit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Insights_Cache")
data class InsightsCacheEntity(
    @PrimaryKey val key: String,
    @ColumnInfo(name = "json_data") val jsonData: String,
    @ColumnInfo(name = "created_at") val createdAt: Long
)
