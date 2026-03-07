package com.scribblefit.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import com.scribblefit.feature.profile.domain.model.*
import com.scribblefit.feature.profile.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings(
        parsingMode = ParsingMode.CLOUD,
        aiProvider = LLMProvider.PROXY,
        weightUnit = WeightUnit.LBS,
        themePreference = ThemePreference.SYSTEM
    ),
    val apiKey: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val secureKeyStorage: SecureKeyStorage,
    private val navigator: com.scribblefit.core.navigation.Navigator
) : ViewModel() {

    private val _apiKey = MutableStateFlow("")
    
    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.getSettings(),
        _apiKey
    ) { settings, key ->
        SettingsUiState(
            settings = settings,
            apiKey = key,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    init {
        viewModelScope.launch {
            _apiKey.value = secureKeyStorage.getApiKey() ?: ""
        }
    }

    fun updateParsingMode(mode: ParsingMode) {
        updateSettings { it.copy(parsingMode = mode) }
    }

    fun updateApiKey(key: String) {
        _apiKey.value = key
        viewModelScope.launch {
            secureKeyStorage.saveApiKey(key)
        }
    }

    fun updateProvider(provider: LLMProvider) {
        updateSettings { it.copy(aiProvider = provider) }
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

    private fun updateSettings(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            val current = uiState.value.settings
            settingsRepository.updateSettings(transform(current))
        }
    }
}
