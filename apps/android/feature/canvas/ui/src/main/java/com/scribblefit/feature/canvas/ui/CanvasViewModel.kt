package com.scribblefit.feature.canvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.canvas.domain.model.FeedItem
import com.scribblefit.feature.canvas.domain.repository.CanvasRepository
import com.scribblefit.feature.canvas.domain.usecase.ConfirmWorkoutUseCase
import com.scribblefit.feature.canvas.domain.usecase.ExecuteQuickActionUseCase
import com.scribblefit.feature.canvas.domain.usecase.ProcessScribbleUseCase
import com.scribblefit.feature.canvas.domain.usecase.QuickActionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

private const val FLOW_TIMEOUT_MS = 5_000L

data class CanvasUiState(
    val greeting: String = "",
    val userName: String = "George",
    val quickActions: List<QuickActionType> = QuickActionType.entries,
    val homeSuggestion: AnalysisSuggestion? = null,
    val scribbleText: String = "",
    val feedItems: List<FeedItem> = emptyList(),
    val isSyncing: Boolean = false,
    val isRecording: Boolean = false
)

@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val canvasRepository: CanvasRepository,
    private val processScribbleUseCase: ProcessScribbleUseCase,
    private val confirmWorkoutUseCase: ConfirmWorkoutUseCase,
    private val executeQuickActionUseCase: ExecuteQuickActionUseCase
) : ViewModel() {

    private val _scribbleText = MutableStateFlow("")
    private val _extras = MutableStateFlow(CanvasExtras())

    val uiState: StateFlow<CanvasUiState> = combine(
        canvasRepository.getFeed(),
        _scribbleText,
        _extras
    ) { feedItems, scribbleText, extras ->
        CanvasUiState(
            greeting = getGreeting(),
            scribbleText = scribbleText,
            feedItems = feedItems,
            homeSuggestion = extras.homeSuggestion,
            isRecording = extras.isRecording
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS), CanvasUiState())

    fun onTextChange(newText: String) {
        _scribbleText.update { newText }
    }

    fun submitScribble() {
        val text = _scribbleText.value.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            processScribbleUseCase.execute(text)
            _scribbleText.update { "" }
        }
    }

    fun onQuickActionClick(type: QuickActionType) {
        viewModelScope.launch { executeQuickActionUseCase.execute(type) }
    }

    fun onRetryScribble(id: String) {
        viewModelScope.launch { canvasRepository.retryScribble(id) }
    }

    fun onConfirmClick(confirmation: FeedItem.Confirmation) {
        viewModelScope.launch { confirmWorkoutUseCase.execute(confirmation) }
    }

    fun onMicClick() {
        _extras.update { it.copy(isRecording = !it.isRecording) }
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < MORNING_CUTOFF -> "Good morning"
            hour < AFTERNOON_CUTOFF -> "Good afternoon"
            else -> "Good evening"
        }
    }

    private data class CanvasExtras(
        val homeSuggestion: AnalysisSuggestion? = null,
        val isRecording: Boolean = false
    )

    companion object {
        private const val MORNING_CUTOFF = 12
        private const val AFTERNOON_CUTOFF = 17
    }
}
