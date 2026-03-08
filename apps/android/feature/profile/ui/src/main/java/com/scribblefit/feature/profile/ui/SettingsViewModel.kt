package com.scribblefit.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import com.scribblefit.feature.profile.domain.model.AppSettings
import com.scribblefit.feature.profile.domain.model.ParsingMode
import com.scribblefit.feature.profile.domain.model.ThemePreference
import com.scribblefit.feature.profile.domain.model.WeightUnit
import com.scribblefit.feature.profile.domain.repository.ModelRepository
import com.scribblefit.feature.profile.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FLOW_TIMEOUT_MS = 5_000L

data class SettingsUiState(
    val settings: AppSettings = AppSettings(
        parsingMode = ParsingMode.CLOUD,
        aiProvider = LLMProvider.PROXY,
        weightUnit = WeightUnit.LBS,
        themePreference = ThemePreference.SYSTEM
    ),
    val apiKey: String = "",
    val isLoading: Boolean = true,
    val availableModels: List<String> = emptyList(),
    val isLoadingModels: Boolean = false,
    val modelLoadError: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val modelRepository: ModelRepository,
    private val secureKeyStorage: SecureKeyStorage,
    private val navigator: com.scribblefit.core.navigation.Navigator
) : ViewModel() {

    private val _apiKey = MutableStateFlow("")
    private val _availableModels = MutableStateFlow<List<String>>(emptyList())
    private val _isLoadingModels = MutableStateFlow(false)
    private val _modelLoadError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.getSettings(),
        _apiKey,
        _availableModels,
        _isLoadingModels,
        _modelLoadError
    ) { settings, key, models, loadingModels, error ->
        SettingsUiState(
            settings = settings,
            apiKey = key,
            isLoading = false,
            availableModels = models,
            isLoadingModels = loadingModels,
            modelLoadError = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
        initialValue = SettingsUiState()
    )

    init {
        viewModelScope.launch {
            _apiKey.value = secureKeyStorage.getApiKey() ?: ""
            val settings = settingsRepository.getSettings().first()
            if ((settings.aiProvider == LLMProvider.OPENAI || settings.aiProvider == LLMProvider.GEMINI) && _apiKey.value.isNotEmpty()) {
                fetchModels(settings.aiProvider, _apiKey.value)
            }
        }
    }

    fun updateParsingMode(mode: ParsingMode) {
        updateSettings { it.copy(parsingMode = mode) }
    }

    fun updateApiKey(key: String) {
        _apiKey.value = key
        viewModelScope.launch {
            secureKeyStorage.saveApiKey(key)
            val provider = uiState.value.settings.aiProvider
            if ((provider == LLMProvider.OPENAI || provider == LLMProvider.GEMINI) && key.isNotEmpty()) {
                fetchModels(provider, key)
            } else {
                _availableModels.value = emptyList()
            }
        }
    }

    fun updateProvider(provider: LLMProvider) {
        updateSettings { it.copy(aiProvider = provider, selectedModel = "") }
        _availableModels.value = emptyList()
        val key = _apiKey.value
        if ((provider == LLMProvider.OPENAI || provider == LLMProvider.GEMINI) && key.isNotEmpty()) {
            viewModelScope.launch { fetchModels(provider, key) }
        }
    }

    fun updateModel(model: String) {
        updateSettings { it.copy(selectedModel = model) }
    }

    fun updateWeightUnit(unit: WeightUnit) {
        updateSettings { it.copy(weightUnit = unit) }
    }

    fun updateTheme(theme: ThemePreference) {
        updateSettings { it.copy(themePreference = theme) }
    }

    fun onBackClick() {
        navigator.goBack()
    }

    fun onClearDataClick() {
        viewModelScope.launch {
            settingsRepository.clearAllData()
        }
    }

    fun fetchModels() {
        val provider = uiState.value.settings.aiProvider
        val key = _apiKey.value
        if (key.isNotEmpty()) {
            viewModelScope.launch { fetchModels(provider, key) }
        }
    }

    private suspend fun fetchModels(provider: LLMProvider, apiKey: String) {
        _isLoadingModels.value = true
        _modelLoadError.value = null
        try {
            val models = modelRepository.fetchModels(provider, apiKey)
            _availableModels.value = models
            if (uiState.value.settings.selectedModel.isEmpty() && models.isNotEmpty()) {
                updateModel(models.first())
            }
        } catch (e: Exception) {
            _modelLoadError.value = "Failed to load models"
        }
        _isLoadingModels.value = false
    }

    private fun updateSettings(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            val current = uiState.value.settings
            settingsRepository.updateSettings(transform(current))
        }
    }
}
