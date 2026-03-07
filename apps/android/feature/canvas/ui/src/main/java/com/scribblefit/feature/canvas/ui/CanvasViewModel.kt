package com.scribblefit.feature.canvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.analytics.domain.repository.AnalysisRepository
import com.scribblefit.feature.canvas.domain.model.*
import com.scribblefit.feature.canvas.domain.repository.CanvasRepository
import com.scribblefit.feature.canvas.domain.usecase.ConfirmWorkoutUseCase
import com.scribblefit.feature.canvas.domain.usecase.ExecuteQuickActionUseCase
import com.scribblefit.feature.canvas.domain.usecase.ProcessScribbleUseCase
import com.scribblefit.feature.canvas.domain.usecase.QuickActionType
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.ParsedExercise
import com.scribblefit.feature.ai.domain.model.ParsedSet
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID
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
    private val confirmWorkoutUseCase: ConfirmWorkoutUseCase,
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

    init {
        viewModelScope.launch {
            if (canvasRepository.getFeed().first().isEmpty()) {
                seedTestData()
            }
        }
    }

    private suspend fun seedTestData() {
        val now = System.currentTimeMillis()
        
        // 1. Initial AI Prompt
        canvasRepository.addScribble("Ready for a Push day? 💪") // This actually adds a scribble, but for testing we'll manually add other types
        
        // 2. A completed scribble
        val scribbleId = UUID.randomUUID().toString()
        canvasRepository.addScribble("Bench 135x5, 135x5")
        
        // 3. A confirmation card for that scribble
        canvasRepository.addConfirmation(
            FeedItem.Confirmation(
                id = UUID.randomUUID().toString(),
                timestamp = now + 1000,
                workout = ParsedWorkout(
                    date = "2024-05-20",
                    location = "Home Gym",
                    exercises = listOf(
                        ParsedExercise(
                            canonicalName = "Bench Press",
                            sets = listOf(
                                ParsedSet(135.0, 5),
                                ParsedSet(135.0, 5)
                            )
                        )
                    )
                ),
                scribbleId = scribbleId
            )
        )

        // 4. An insight
        canvasRepository.addInsight(
            FeedItem.Insight(
                id = UUID.randomUUID().toString(),
                timestamp = now + 2000,
                text = "New Volume PR on Bench! 🔥",
                emoji = "🏆"
            )
        )
    }

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

    fun onConfirmClick(confirmation: FeedItem.Confirmation) {
        viewModelScope.launch {
            confirmWorkoutUseCase(confirmation.workout)
            canvasRepository.addInsight(FeedItem.Insight(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                text = "Workout saved to ledger!",
                emoji = "✅"
            ))
            canvasRepository.removeFeedItem(confirmation.id)
        }
    }

    fun onEditClick(confirmation: FeedItem.Confirmation) {
        val scribble = uiState.value.feedItems.find { it.id == confirmation.scribbleId } as? FeedItem.Scribble
        scribble?.let { scribble ->
            _internalState.update { it.copy(scribbleText = scribble.rawText) }
        }
        viewModelScope.launch {
            canvasRepository.removeFeedItem(confirmation.id)
            canvasRepository.removeFeedItem(confirmation.scribbleId)
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
    }

    private fun stopRecording() {
        _internalState.update { it.copy(isRecording = false) }
        onTextChange("Bench 135x5, 135x5")
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
