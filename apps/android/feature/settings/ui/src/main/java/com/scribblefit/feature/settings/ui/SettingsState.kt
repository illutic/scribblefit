package com.scribblefit.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight

data class SettingsState(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val isDynamicTheme: Boolean = false,
    val weightUnit: Weight = Weight.KGS,
    val llmProvider: LLMProvider = LLMProvider.LOCAL,
    val preferredModel: String = "",
    val apiKey: String = "",
    val isApiKeyVisible: Boolean = false,
    val connectionTestStatus: ConnectionTestStatus = ConnectionTestStatus.Idle,
    val availableModels: List<String> = emptyList(),
    val isLocalSupported: Boolean = true,
    val isFetchingModels: Boolean = false,
    val showModelSelection: Boolean = false,
    val exportData: String? = null,
    val showClearDataDialog: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val themeLabel: String
        @Composable @ReadOnlyComposable get() = when (theme) {
            ThemePreference.SYSTEM -> stringResource(R.string.settings_theme_system)
            ThemePreference.LIGHT -> stringResource(R.string.settings_theme_light)
            ThemePreference.DARK -> stringResource(R.string.settings_theme_dark)
        }

    val weightUnitLabel: String
        @Composable @ReadOnlyComposable get() = when (weightUnit) {
            Weight.KGS -> stringResource(R.string.settings_unit_kgs)
            Weight.LBS -> stringResource(R.string.settings_unit_lbs)
        }

    val llmProviderLabel: String
        @Composable @ReadOnlyComposable get() = when (llmProvider) {
            LLMProvider.GEMINI -> stringResource(R.string.settings_ai_provider_cloud)
            LLMProvider.LOCAL -> stringResource(R.string.settings_ai_provider_local)
        }

    val connectionStatusMessage: String?
        @Composable @ReadOnlyComposable get() = when (val status = connectionTestStatus) {
            is ConnectionTestStatus.Success -> stringResource(R.string.settings_connection_success)
            is ConnectionTestStatus.Error -> stringResource(
                R.string.settings_connection_error,
                status.message
            )

            else -> null
        }

    // Static Section Titles and Labels resolved via State
    val appearanceTitle: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_appearance)
    val themeTitle: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_theme)
    val themeLightLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_theme_light)
    val themeDarkLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_theme_dark)
    val themeSystemLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_theme_system)
    val unitsTitle: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_units)
    val weightPreferenceLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_weight_preference)
    val aiEngineTitle: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_ai_engine_title)
    val providerLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_ai_provider)
    val modelLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_ai_model)
    val aiModelSelectionTitle: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_ai_model_selection_title)
    val apiKeyTitle: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_api_key_title)
    val apiKeyPlaceholder: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_api_key_placeholder)
    val testConnectionLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_test_connection)
    val dataTitle: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_data_title)
    val exportWorkoutLedgerLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_export_workout_ledger)
    val jsonFormatLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_json_format)
    val clearAllDataLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_clear_data)
    val copyrightLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_copyright)
    val aiLocalUnsupportedError: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_ai_local_unsupported)

    @Composable
    @ReadOnlyComposable
    fun getVersionLabel(version: String): String =
        stringResource(R.string.settings_version, version)
}

sealed interface ConnectionTestStatus {
    object Idle : ConnectionTestStatus
    object Testing : ConnectionTestStatus
    object Success : ConnectionTestStatus
    data class Error(val message: String) : ConnectionTestStatus
}
