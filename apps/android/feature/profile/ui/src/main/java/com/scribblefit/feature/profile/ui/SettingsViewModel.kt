package com.scribblefit.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import com.scribblefit.feature.profile.domain.repository.ModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: SystemConfig,
    val availableModels: List<String> = emptyList(),
    val isLoadingModels: Boolean = false,
    val showApiKeyInput: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val modelRepository: ModelRepository,
    private val secureKeyStorage: SecureKeyStorage
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = configRepository.config
        .map { config ->
            getStateFromConfig(
                config = config,
                apiKey = secureKeyStorage.getApiKey().orEmpty()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsUiState(
                settings = configRepository.config.value
            )
        )

    fun onProviderChanged(provider: LLMProvider) {
        viewModelScope.launch {
            val current = uiState.value.settings
            configRepository.updateConfig(
                current.copy(preferredLlmProvider = provider, preferredModel = null)
            )
        }
    }

    fun onModelSelected(model: String) {
        viewModelScope.launch {
            val current = uiState.value.settings
            configRepository.updateConfig(current.copy(preferredModel = model))
        }
    }

    fun onApiKeySaved(key: String) {
        viewModelScope.launch {
            secureKeyStorage.saveApiKey(key)
            configRepository.updateConfig(
                uiState.value.settings.copy(
                    preferredModel = null,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun onWeightUnitChanged(unit: Weight) {
        viewModelScope.launch {
            val current = uiState.value.settings
            configRepository.updateConfig(current.copy(weightUnit = unit))
        }
    }

    fun onThemeChanged(theme: ThemePreference) {
        viewModelScope.launch {
            val current = uiState.value.settings
            configRepository.updateConfig(current.copy(themePreference = theme))
        }
    }

    fun onClearDataTapped() {
        viewModelScope.launch {
            configRepository.resetConfig()
            secureKeyStorage.clearApiKey()
        }
    }

    private suspend fun getStateFromConfig(
        config: SystemConfig,
        apiKey: String
    ): SettingsUiState {
        val showInput = config.preferredLlmProvider.requiresApiKey
        val models = if (showInput) {
            runCatching {
                modelRepository.fetchModels(
                    provider = config.preferredLlmProvider,
                    apiKey = apiKey
                )
            }.getOrDefault(emptyList())
        } else {
            emptyList()
        }

        return SettingsUiState(
            settings = config,
            availableModels = models,
            showApiKeyInput = showInput && apiKey.isEmpty()
        )
    }
}
