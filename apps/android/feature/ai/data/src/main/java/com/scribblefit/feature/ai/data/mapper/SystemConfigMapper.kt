package com.scribblefit.feature.ai.data.mapper

import com.scribblefit.core.database.model.SystemConfigEntity
import com.scribblefit.feature.ai.domain.model.SystemConfig

fun SystemConfigEntity.toDomain() = SystemConfig(
    promptVersion = promptVersion,
    promptText = promptText,
    exerciseVersion = exerciseVersion,
    preferredLlmProvider = preferredLlmProvider,
    parsingMode = parsingMode,
    weightUnit = weightUnit,
    themePreference = themePreference,
    updatedAt = updatedAt
)

fun SystemConfig.toEntity() = SystemConfigEntity(
    promptVersion = promptVersion,
    promptText = promptText,
    exerciseVersion = exerciseVersion,
    preferredLlmProvider = preferredLlmProvider,
    parsingMode = parsingMode,
    weightUnit = weightUnit,
    themePreference = themePreference,
    updatedAt = updatedAt
)
