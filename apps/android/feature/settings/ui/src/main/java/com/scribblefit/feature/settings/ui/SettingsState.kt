package com.scribblefit.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight
import java.time.LocalDate

data class SettingsState(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val isDynamicTheme: Boolean = false,
    val weightUnit: Weight = Weight.KGS,
    val llmProvider: LLMProvider = LLMProvider.LOCAL,
    val isLocalSupported: Boolean = true,
    val exportData: String? = null,
    val showClearDataDialog: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val version: String = ""
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
    val dataTitle: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_data_title)
    val exportWorkoutLedgerLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_export_workout_ledger)
    val jsonFormatLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_json_format)
    val clearAllDataLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_clear_data)
    val copyrightLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_copyright, LocalDate.now().year)
    val aiLocalUnsupportedError: String @Composable @ReadOnlyComposable get() = stringResource(R.string.settings_ai_local_unsupported)

    @Composable
    @ReadOnlyComposable
    fun getVersionLabel(version: String): String =
        stringResource(R.string.settings_version, version)
}
