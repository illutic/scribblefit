package com.scribblefit.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import com.scribblefit.feature.profile.domain.model.AppSettings
import com.scribblefit.feature.profile.domain.model.ThemePreference
import com.scribblefit.feature.profile.domain.model.WeightUnit
import com.scribblefit.feature.profile.domain.repository.ModelRepository
import com.scribblefit.feature.profile.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FLOW_TIMEOUT_MS = 5_000L

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val availableModels: List<String> = emptyList(),
    val isLoadingModels: Boolean = false,
    val apiKey: String = "",
    val showApiKeyInput: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val modelRepository: ModelRepository,
    private val secureKeyStorage: SecureKeyStorage
) : ViewModel() {

    private val extras = MutableStateFlow(
        SettingsExtras(
            availableModels = emptyList(),
            isLoadingModels = false,
            apiKey = "",
            showApiKeyInput = false
        )
    )

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.getSettings(),
        extras
    ) { settings, ext ->
        SettingsUiState(
            settings = settings,
            availableModels = ext.availableModels,
            isLoadingModels = ext.isLoadingModels,
            apiKey = ext.apiKey,
            showApiKeyInput = ext.showApiKeyInput
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS), SettingsUiState())

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val savedKey = secureKeyStorage.getApiKey() ?: ""
            extras.update { it.copy(apiKey = savedKey) }
        }
    }

    fun onProviderChanged(provider: LLMProvider) {
        viewModelScope.launch {
            val current = uiState.value.settings
            settingsRepository.updateSettings(current.copy(aiProvider = provider, selectedModel = ""))
            val showInput = provider != LLMProvider.PROXY && provider != LLMProvider.LOCAL
            extras.update { it.copy(showApiKeyInput = showInput, availableModels = emptyList()) }
            if (showInput) {
                val existingKey = secureKeyStorage.getApiKey()
                if (!existingKey.isNullOrBlank()) {
                    fetchModels(provider, existingKey)
                }
            }
        }
    }

    fun onModelSelected(model: String) {
        viewModelScope.launch {
            val current = uiState.value.settings
            settingsRepository.updateSettings(current.copy(selectedModel = model))
        }
    }

    fun onApiKeySaved(key: String) {
        viewModelScope.launch {
            secureKeyStorage.saveApiKey(key)
            extras.update { it.copy(apiKey = key) }
            val provider = uiState.value.settings.aiProvider
            fetchModels(provider, key)
        }
    }

    fun onWeightUnitChanged(unit: WeightUnit) {
        viewModelScope.launch {
            val current = uiState.value.settings
            settingsRepository.updateSettings(current.copy(weightUnit = unit))
        }
    }

    fun onThemeChanged(theme: ThemePreference) {
        viewModelScope.launch {
            val current = uiState.value.settings
            settingsRepository.updateSettings(current.copy(themePreference = theme))
        }
    }

    fun onClearDataTapped() {
        viewModelScope.launch {
            settingsRepository.clearAllData()
            secureKeyStorage.clearApiKey()
            extras.update { SettingsExtras(availableModels = emptyList(), isLoadingModels = false, apiKey = "", showApiKeyInput = false) }
        }
    }

    fun fetchModels() {
        val state = uiState.value
        viewModelScope.launch {
            fetchModels(state.settings.aiProvider, state.apiKey)
        }
    }

    private suspend fun fetchModels(provider: LLMProvider, apiKey: String) {
        extras.update { it.copy(isLoadingModels = true) }
        val models = runCatching { modelRepository.fetchModels(provider, apiKey) }.getOrDefault(emptyList())
        extras.update { it.copy(isLoadingModels = false, availableModels = models) }
    }
}

private data class SettingsExtras(
    val availableModels: List<String>,
    val isLoadingModels: Boolean,
    val apiKey: String,
    val showApiKeyInput: Boolean
)
