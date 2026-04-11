package com.scribblefit.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.SecureKeyStorage
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.feature.settings.domain.CheckLocalSupportUseCase
import com.scribblefit.feature.settings.domain.ClearUserDataUseCase
import com.scribblefit.feature.settings.domain.ExportUserDataUseCase
import com.scribblefit.feature.settings.domain.GetAvailableModelsUseCase
import com.scribblefit.feature.settings.domain.TestConnectionUseCase
import com.scribblefit.feature.settings.domain.UpdateApiKeyUseCase
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
    private val secureKeyStorage: SecureKeyStorage,
    private val clearUserDataUseCase: ClearUserDataUseCase,
    private val exportUserDataUseCase: ExportUserDataUseCase,
    private val testConnectionUseCase: TestConnectionUseCase,
    private val updateSystemConfigUseCase: UpdateSystemConfigUseCase,
    private val updateApiKeyUseCase: UpdateApiKeyUseCase,
    private val getAvailableModelsUseCase: GetAvailableModelsUseCase,
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
                        preferredModel = config.preferredModel ?: ""
                    )
                }

                // If Gemini is selected and we have a key (or just on init), 
                // we might want to fetch models if we don't have them
                if (config.preferredLlmProvider == LLMProvider.GEMINI) {
                    val apiKey = secureKeyStorage.getApiKey()
                    if (!apiKey.isNullOrBlank() && _state.value.availableModels.isEmpty()) {
                        fetchModels(apiKey)
                    }
                }
            }
        }

        // Load API Key
        val apiKey = secureKeyStorage.getApiKey() ?: ""
        viewModelScope.launch {
            _state.update {
                it.copy(
                    apiKey = apiKey,
                    isLocalSupported = checkLocalSupportUseCase()
                )
            }
        }

        // Initial fetch of models if we have an API key and are on Gemini
        if (apiKey.isNotBlank() && configRepository.config.value.preferredLlmProvider == LLMProvider.GEMINI) {
            fetchModels(apiKey)
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

            is SettingsIntent.ChangeAIModel -> {
                updateConfig { it.copy(preferredModel = intent.model) }
                _state.update { it.copy(showModelSelection = false) }
            }

            SettingsIntent.ShowModelSelection -> {
                _state.update { it.copy(showModelSelection = true) }
            }

            SettingsIntent.DismissModelSelection -> {
                _state.update { it.copy(showModelSelection = false) }
            }

            is SettingsIntent.ChangeApiKey -> {
                _state.update {
                    it.copy(
                        apiKey = intent.apiKey,
                        connectionTestStatus = ConnectionTestStatus.Idle
                    )
                }
                viewModelScope.launch {
                    updateApiKeyUseCase(intent.apiKey)
                }
            }

            SettingsIntent.ToggleApiKeyVisibility -> {
                _state.update { it.copy(isApiKeyVisible = !it.isApiKeyVisible) }
            }

            SettingsIntent.TestConnection -> testConnection()
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

    private fun testConnection() {
        viewModelScope.launch {
            _state.update { it.copy(connectionTestStatus = ConnectionTestStatus.Testing) }
            val result = testConnectionUseCase(_state.value.apiKey)
            if (result.isSuccess) {
                _state.update { it.copy(connectionTestStatus = ConnectionTestStatus.Success) }
                fetchModels(_state.value.apiKey)
            } else {
                _state.update { s ->
                    s.copy(
                        connectionTestStatus = ConnectionTestStatus.Error(
                            result.exceptionOrNull()?.message ?: "Unknown Error"
                        )
                    )
                }
            }
        }
    }

    private fun fetchModels(apiKey: String) {
        if (apiKey.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isFetchingModels = true) }
            getAvailableModelsUseCase(apiKey)
                .onSuccess { models ->
                    _state.update { it.copy(availableModels = models, isFetchingModels = false) }
                }
                .onFailure {
                    _state.update { it.copy(isFetchingModels = false) }
                }
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
