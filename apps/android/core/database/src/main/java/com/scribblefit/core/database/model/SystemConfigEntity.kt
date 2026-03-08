package com.scribblefit.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scribblefit.feature.ai.domain.model.LLMProvider

@Entity(tableName = "System_Config")
data class SystemConfigEntity(
    @PrimaryKey
    val id: String = "config",
    @ColumnInfo(name = "prompt_version")
    val promptVersion: String,
    @ColumnInfo(name = "prompt_text")
    val promptText: String,
    @ColumnInfo(name = "exercise_version")
    val exerciseVersion: String = "0.0.0",
    @ColumnInfo(name = "preferred_llm_provider")
    val preferredLlmProvider: LLMProvider = LLMProvider.PROXY,
    @ColumnInfo(name = "preferred_model", defaultValue = "")
    val preferredModel: String = "",
    @ColumnInfo(name = "parsing_mode")
    val parsingMode: String = "CLOUD",
    @ColumnInfo(name = "weight_unit")
    val weightUnit: String = "LBS",
    @ColumnInfo(name = "theme_preference")
    val themePreference: String = "SYSTEM",
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
