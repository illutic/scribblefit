package com.scribblefit.feature.settings.ui

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LocalConfig
import com.scribblefit.feature.settings.domain.CheckLocalSupportUseCase
import com.scribblefit.feature.settings.domain.ClearUserDataUseCase
import com.scribblefit.feature.settings.domain.ExportUserDataUseCase
import com.scribblefit.feature.settings.domain.UpdateSystemConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val configRepository: ConfigRepository,
    private val clearUserDataUseCase: ClearUserDataUseCase,
    private val exportUserDataUseCase: ExportUserDataUseCase,
    private val updateSystemConfigUseCase: UpdateSystemConfigUseCase,
    private val checkLocalSupportUseCase: CheckLocalSupportUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        // Observe system config
        viewModelScope.launch {
            configRepository.config.collect { config ->
                _state.update {
                    it.copy(
                        theme = config.localConfig.themePreference,
                        isDynamicTheme = config.localConfig.isDynamicTheme,
                        weightUnit = config.localConfig.weightUnit,
                        llmProvider = config.localConfig.preferredLlmProvider,
                        version = getVersionInfo() ?: ""
                    )
                }
            }
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLocalSupported = checkLocalSupportUseCase()
                )
            }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ChangeTheme -> updateConfig { it.copy(themePreference = intent.theme) }
            is SettingsIntent.ToggleDynamicTheme -> updateConfig { it.copy(isDynamicTheme = intent.enabled) }
            is SettingsIntent.ChangeWeightUnit -> updateConfig { it.copy(weightUnit = intent.unit) }
            is SettingsIntent.ChangeAIProvider -> {
                updateConfig { it.copy(preferredLlmProvider = intent.provider) }
            }

            SettingsIntent.ExportData -> exportData()
            SettingsIntent.ClearAllData -> clearAllData()
            SettingsIntent.ShowClearDataDialog -> _state.update { it.copy(showClearDataDialog = true) }
            SettingsIntent.DismissClearDataDialog -> _state.update { it.copy(showClearDataDialog = false) }
            SettingsIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
            SettingsIntent.DismissExport -> _state.update { it.copy(exportData = null) }
        }
    }

    private fun updateConfig(update: (LocalConfig) -> LocalConfig) {
        viewModelScope.launch {
            val currentConfig = configRepository.config.value
            updateSystemConfigUseCase(currentConfig.copy(localConfig = update(currentConfig.localConfig)))
        }
    }

    private fun exportData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                exportUserDataUseCase().collect { data ->
                    _state.update { it.copy(exportData = data) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message ?: "Failed to export data") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun clearAllData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, showClearDataDialog = false) }
            try {
                clearUserDataUseCase()
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message ?: "Failed to clear data") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun getVersionInfo(): String? =
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName ?: "Unknown"
            val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
            "$versionName ($versionCode)"
        } catch (_: Exception) {
            null
        }
}
