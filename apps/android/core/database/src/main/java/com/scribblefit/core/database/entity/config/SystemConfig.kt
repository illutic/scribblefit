package com.scribblefit.core.database.entity.config

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_config")
data class SystemConfig(
    @PrimaryKey val id: Int = 0, // Single row with a fixed key
    val summaryPrompt: String,
    val suggestionPrompt: String,
    val insightPrompt: String,
    val parsePrompt: String,
    val preferredLlmProvider: String,
    val updatedAt: Long,
    val preferredModel: String?,
    val weightUnit: String,
    val themePreference: String,
)
