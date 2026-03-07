package com.scribblefit.feature.canvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.canvas.domain.model.FeedItem
import com.scribblefit.feature.canvas.domain.repository.CanvasRepository
import com.scribblefit.feature.canvas.domain.usecase.ProcessScribbleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val canvasRepository: CanvasRepository,
    private val processScribbleUseCase: ProcessScribbleUseCase
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
}
