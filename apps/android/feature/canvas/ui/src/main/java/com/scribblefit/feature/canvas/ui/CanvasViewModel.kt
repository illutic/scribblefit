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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class CanvasUiState(
    val greeting: String = "",
    val userName: String = "George", // Placeholder, could be from a UserProfileRepository
    val quickActions: List<QuickActionType> = listOf(
        QuickActionType.REPEAT_LAST,
        QuickActionType.RUN_5K,
        QuickActionType.REST_DAY
    ),
    val homeSuggestion: AnalysisSuggestion? = null
)

@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val canvasRepository: CanvasRepository,
    private val analysisRepository: AnalysisRepository,
    private val processScribbleUseCase: ProcessScribbleUseCase,
    private val executeQuickActionUseCase: ExecuteQuickActionUseCase
) : ViewModel() {

    private val _scribbleText = MutableStateFlow("")
    val scribbleText: StateFlow<String> = _scribbleText

    val feedItems: StateFlow<List<FeedItem>> = canvasRepository.getFeed()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    val uiState: StateFlow<CanvasUiState> = analysisRepository.getHomeSuggestion()
        .map { suggestion ->
            CanvasUiState(
                greeting = getGreeting(),
                homeSuggestion = suggestion
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CanvasUiState(greeting = getGreeting())
        )

    fun onTextChange(newText: String) {
        _scribbleText.value = newText
    }

    fun submitScribble() {
        val text = _scribbleText.value
        if (text.isBlank()) return

        viewModelScope.launch {
            _isSyncing.value = true
            processScribbleUseCase(text)
            _scribbleText.value = ""
            _isSyncing.value = false
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
