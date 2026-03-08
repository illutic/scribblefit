package com.scribblefit.feature.profile.domain.model

import com.scribblefit.feature.ai.domain.model.LLMProvider

data class UserStats(
    val totalWorkouts: Int,
    val lifetimeVolume: Double,
    val prCount: Int,
    val joinDate: Long
)

data class AppSettings(
    val parsingMode: ParsingMode = ParsingMode.CLOUD,
    val aiProvider: LLMProvider = LLMProvider.PROXY,
    val weightUnit: WeightUnit = WeightUnit.LBS,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val selectedModel: String = ""
)

enum class ParsingMode { CLOUD, PERSONAL }
enum class WeightUnit { LBS, KG }
enum class ThemePreference { LIGHT, DARK, SYSTEM }
