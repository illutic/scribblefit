package com.scribblefit.core.database.entity.config

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.LocalConfig
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight

@Entity(tableName = "system_config")
data class SystemConfigEntity(
    @PrimaryKey val id: Int = 0,
    val preferredLlmProvider: String,
    val weightUnit: String,
    val themePreference: String,
    val isDynamicTheme: Boolean
)

fun SystemConfigEntity.toDomain(): SystemConfig = SystemConfig(
    localConfig = LocalConfig(
        preferredLlmProvider = LLMProvider.valueOf(preferredLlmProvider),
        weightUnit = Weight.valueOf(weightUnit),
        themePreference = ThemePreference.valueOf(themePreference),
        isDynamicTheme = isDynamicTheme
    )
)

fun SystemConfig.toEntity(): SystemConfigEntity = SystemConfigEntity(
    preferredLlmProvider = localConfig.preferredLlmProvider.name,
    weightUnit = localConfig.weightUnit.name,
    themePreference = localConfig.themePreference.name,
    isDynamicTheme = localConfig.isDynamicTheme
)
