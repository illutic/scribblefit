package com.scribblefit.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.feature.settings.domain.CheckLocalSupportUseCase
import com.scribblefit.feature.settings.domain.ClearUserDataUseCase
import com.scribblefit.feature.settings.domain.ExportUserDataUseCase
import com.scribblefit.feature.settings.domain.UpdateSystemConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
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
                        theme = config.themePreference,
                        isDynamicTheme = config.isDynamicTheme,
                        weightUnit = config.weightUnit,
                        llmProvider = config.preferredLlmProvider,
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

    private fun updateConfig(update: (SystemConfig) -> SystemConfig) {
        viewModelScope.launch {
            val currentConfig = configRepository.config.value
            updateSystemConfigUseCase(update(currentConfig))
        }
    }

    private fun exportData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            exportUserDataUseCase().collect { data ->
                _state.update { it.copy(exportData = data, isLoading = false) }
            }
        }
    }

    private fun clearAllData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, showClearDataDialog = false) }
            clearUserDataUseCase()
            _state.update { it.copy(isLoading = false) }
        }
    }
}
