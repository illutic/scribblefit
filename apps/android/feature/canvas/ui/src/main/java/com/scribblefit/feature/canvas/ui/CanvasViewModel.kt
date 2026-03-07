package com.scribblefit.feature.canvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.analytics.domain.repository.AnalysisRepository
import com.scribblefit.feature.canvas.domain.model.FeedItem
import com.scribblefit.feature.canvas.domain.repository.CanvasRepository
import com.scribblefit.feature.canvas.domain.usecase.ExecuteQuickActionUseCase
import com.scribblefit.feature.canvas.domain.usecase.ProcessScribbleUseCase
import com.scribblefit.feature.canvas.domain.usecase.QuickActionType
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class CanvasUiState(
    val greeting: String = "",
    val userName: String = "George",
    val quickActions: List<QuickActionType> = listOf(
        QuickActionType.REPEAT_LAST,
        QuickActionType.RUN_5K,
        QuickActionType.REST_DAY
    ),
    val homeSuggestion: AnalysisSuggestion? = null,
    val scribbleText: String = "",
    val feedItems: List<FeedItem> = emptyList(),
    val isSyncing: Boolean = false,
    val isRecording: Boolean = false
)

@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val canvasRepository: CanvasRepository,
    private val analysisRepository: AnalysisRepository,
    private val processScribbleUseCase: ProcessScribbleUseCase,
    private val executeQuickActionUseCase: ExecuteQuickActionUseCase,
    private val navigator: Navigator
) : ViewModel() {

    private val _internalState = MutableStateFlow(CanvasUiState(greeting = getGreeting()))
    
    val uiState: StateFlow<CanvasUiState> = combine(
        _internalState,
        canvasRepository.getFeed(),
        analysisRepository.getHomeSuggestion()
    ) { state, feed, suggestion ->
        state.copy(
            feedItems = feed,
            homeSuggestion = suggestion
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _internalState.value
    )

    fun onTextChange(newText: String) {
        _internalState.update { it.copy(scribbleText = newText) }
    }

    fun submitScribble() {
        val text = _internalState.value.scribbleText
        if (text.isBlank()) return

        viewModelScope.launch {
            _internalState.update { it.copy(isSyncing = true) }
            processScribbleUseCase(text)
            _internalState.update { it.copy(isSyncing = false, scribbleText = "") }
        }
    }

    fun onQuickActionClick(actionType: QuickActionType) {
        viewModelScope.launch {
            executeQuickActionUseCase(actionType)
        }
    }

    fun onRetryScribble(id: String) {
        viewModelScope.launch {
            canvasRepository.retryScribble(id)
        }
    }

    fun onMenuClick() {
        navigator.navigateTo(Screen.Settings)
    }

    fun onMicClick() {
        if (_internalState.value.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        _internalState.update { it.copy(isRecording = true) }
        // Simulated voice capture delay and result
    }

    private fun stopRecording() {
        _internalState.update { it.copy(isRecording = false) }
        onTextChange("Simulated voice input...")
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "MORNING"
            in 12..16 -> "AFTERNOON"
            in 17..20 -> "EVENING"
            else -> "NIGHT"
        }
    }
}
