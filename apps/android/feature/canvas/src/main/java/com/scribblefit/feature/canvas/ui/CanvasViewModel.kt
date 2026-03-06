package com.scribblefit.feature.canvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.ai.domain.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _scribbleText = MutableStateFlow("")
    val scribbleText: StateFlow<String> = _scribbleText

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
            syncRepository.enqueueScribble(text)
            _scribbleText.value = ""
            _isSyncing.value = false
        }
    }
}
