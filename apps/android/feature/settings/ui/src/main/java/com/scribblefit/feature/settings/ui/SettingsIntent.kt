package com.scribblefit.feature.settings.ui

import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight

sealed interface SettingsIntent {
    data class ChangeTheme(val theme: ThemePreference) : SettingsIntent
    data class ToggleDynamicTheme(val enabled: Boolean) : SettingsIntent
    data class ChangeWeightUnit(val unit: Weight) : SettingsIntent
    data class ChangeAIProvider(val provider: LLMProvider) : SettingsIntent
    data class ChangeAIModel(val model: String) : SettingsIntent
    object ShowModelSelection : SettingsIntent
    object DismissModelSelection : SettingsIntent
    data class ChangeApiKey(val apiKey: String) : SettingsIntent
    object ToggleApiKeyVisibility : SettingsIntent
    object TestConnection : SettingsIntent
    object ExportData : SettingsIntent
    object ClearAllData : SettingsIntent
    object ShowClearDataDialog : SettingsIntent
    object DismissClearDataDialog : SettingsIntent
    object DismissError : SettingsIntent
    object DismissExport : SettingsIntent
}
