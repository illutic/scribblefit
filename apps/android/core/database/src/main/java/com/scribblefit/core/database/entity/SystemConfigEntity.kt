package com.scribblefit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "System_Config")
data class SystemConfigEntity(
    @PrimaryKey val id: String = "config",
    @ColumnInfo(name = "prompt_version") val promptVersion: String,
    @ColumnInfo(name = "prompt_text") val promptText: String,
    @ColumnInfo(name = "exercise_version") val exerciseVersion: String = "0.0.0",
    @ColumnInfo(name = "preferred_llm_provider") val preferredLlmProvider: String = "proxy",
    @ColumnInfo(name = "preferred_model") val preferredModel: String = "",
    @ColumnInfo(name = "parsing_mode") val parsingMode: String = "managed",
    @ColumnInfo(name = "weight_unit") val weightUnit: String = "lbs",
    @ColumnInfo(name = "theme_preference") val themePreference: String = "system",
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
