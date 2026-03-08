package com.scribblefit.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import com.scribblefit.feature.profile.domain.model.*
import com.scribblefit.feature.profile.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Named

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
    private val secureKeyStorage: SecureKeyStorage,
    private val navigator: com.scribblefit.core.navigation.Navigator,
    @Named("base") private val httpClient: HttpClient
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
        started = SharingStarted.WhileSubscribed(5000),
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
            val models = when (provider) {
                LLMProvider.OPENAI -> fetchOpenAIModels(apiKey)
                LLMProvider.GEMINI -> fetchGeminiModels(apiKey)
                else -> emptyList()
            }
            _availableModels.value = models
            // Auto-select first if no model set
            if (uiState.value.settings.selectedModel.isEmpty() && models.isNotEmpty()) {
                updateModel(models.first())
            }
        } catch (e: Exception) {
            _modelLoadError.value = "Failed to load models"
        }
        _isLoadingModels.value = false
    }

    private suspend fun fetchOpenAIModels(apiKey: String): List<String> {
        @Serializable
        data class ModelItem(val id: String)
        @Serializable
        data class ModelList(val data: List<ModelItem>)

        val response = httpClient.get("https://api.openai.com/v1/models") {
            header("Authorization", "Bearer $apiKey")
        }.body<ModelList>()

        return response.data
            .map { it.id }
            .filter { it.startsWith("gpt-") || it.startsWith("o1") || it.startsWith("o3") }
            .sorted()
    }

    private suspend fun fetchGeminiModels(apiKey: String): List<String> {
        @Serializable
        data class ModelItem(
            val name: String,
            val supportedGenerationMethods: List<String> = emptyList()
        )
        @Serializable
        data class ModelList(val models: List<ModelItem>)

        val response = httpClient.get("https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey")
            .body<ModelList>()

        return response.models
            .filter { it.supportedGenerationMethods.contains("generateContent") }
            .map { it.name }
            .sorted()
    }

    private fun updateSettings(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            val current = uiState.value.settings
            settingsRepository.updateSettings(transform(current))
        }
    }
}
