package com.scribblefit.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "System_Config")
data class SystemConfigEntity(
    @PrimaryKey
    val id: String = "config", // Singleton-like entry
    @ColumnInfo(name = "prompt_version")
    val promptVersion: String,
    @ColumnInfo(name = "prompt_text")
    val promptText: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
