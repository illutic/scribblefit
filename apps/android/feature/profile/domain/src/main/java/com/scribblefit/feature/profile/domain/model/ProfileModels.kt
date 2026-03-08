package com.scribblefit.feature.profile.domain.model

data class UserStats(
    val totalWorkouts: Int,
    val lifetimeVolume: Double,
    val prCount: Int,
    val joinDate: Long
)

data class AppSettings(
    val parsingMode: ParsingMode,
    val aiProvider: com.scribblefit.feature.ai.domain.model.LLMProvider,
    val weightUnit: WeightUnit,
    val themePreference: ThemePreference,
    val selectedModel: String = ""
)

enum class ParsingMode { CLOUD, PERSONAL }
enum class WeightUnit { LBS, KG }
enum class ThemePreference { LIGHT, DARK, SYSTEM }
