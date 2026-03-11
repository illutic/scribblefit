package com.scribblefit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "System_Config")
data class SystemConfigEntity(
    @PrimaryKey val id: String = "config",
    @ColumnInfo(name = "summary_prompt") val summaryPrompt: String,
    @ColumnInfo(name = "suggestion_prompt") val suggestionPrompt: String,
    @ColumnInfo(name = "insight_prompt") val insightPrompt: String,
    @ColumnInfo(name = "parse_prompt") val parsePrompt: String,
    @ColumnInfo(name = "preferred_llm_provider") val preferredLlmProvider: String = "local",
    @ColumnInfo(name = "preferred_model") val preferredModel: String? = null,
    @ColumnInfo(name = "weight_unit") val weightUnit: String = "lbs",
    @ColumnInfo(name = "theme_preference") val themePreference: String = "system",
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
