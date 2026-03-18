package com.scribblefit.feature.canvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.canvas.domain.ParsePendingScribblesUseCase
import com.scribblefit.feature.scribble.domain.usecase.AddRawScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.EditScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsCompleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CanvasViewModel
    @Inject
    constructor(
        getScribblesByDateUseCase: GetScribblesByDateUseCase,
        private val parsePendingScribblesUseCase: ParsePendingScribblesUseCase,
        private val addRawScribbleUseCase: AddRawScribbleUseCase,
        private val editScribbleUseCase: EditScribbleUseCase,
        private val removeScribbleUseCase: RemoveScribbleUseCase,
        private val updateScribbleAsCompleteUseCase: UpdateScribbleAsCompleteUseCase,
        private val navigator: Navigator,
    ) : ViewModel() {
        private val _state = MutableStateFlow(CanvasState())
        private val currentDate =
            _state
                .map { it.currentDate }
                .distinctUntilChanged()

        val state =
            combine(
                _state,
                getScribblesByDateUseCase(currentDate),
            ) { state, scribbles -> state.copy(scribbles = scribbles) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CanvasState())

        fun onIntent(intent: CanvasIntent) {
            when (intent) {
                is CanvasIntent.UpdateScribbleText -> {
                    _state.update { it.copy(currentScribbleText = intent.text) }
                }

                is CanvasIntent.AddScribble -> {
                    addScribble(intent.scribble)
                }

                is CanvasIntent.NavigateToScreen -> {
                    navigator.navigateTo(intent.screen)
                }

                is CanvasIntent.ClickOnScribble -> {
                    scribbleClicked(intent.scribble)
                }

                is CanvasIntent.ConfirmScribble -> {
                    completeScribble(intent.scribble)
                }

                is CanvasIntent.DeleteScribble -> {
                    deleteScribble(intent.scribble)
                }

                CanvasIntent.DismissScribbleDialog -> {
                    dismissScribbleDialog()
                }

                CanvasIntent.NavigateBack -> {
                    navigator.goBack()
                }

                is CanvasIntent.UpdateScribble -> {
                    editScribble(intent.scribble)
                }

                CanvasIntent.OnPreviousDayClick -> {
                    _state.update { it.copy(currentDate = it.currentDate.minusDays(1)) }
                }

                CanvasIntent.OnNextDayClick -> {
                    _state.update { it.copy(currentDate = it.currentDate.plusDays(1)) }
                }
            }
        }

        private fun addScribble(text: String) {
            viewModelScope.launch {
                val editingId = _state.value.editingScribbleId
                _state.update { it.copy(currentScribbleText = "", editingScribbleId = null) }

                val result =
                    if (editingId != null) {
                        editScribbleUseCase(editingId, text)
                    } else {
                        addRawScribbleUseCase(text, _state.value.currentDate)
                    }

                result.onFailure { e ->
                    _state.update { it.copy(error = e) }
                }
            }
        }

        private fun scribbleClicked(scribble: Scribble) {
            viewModelScope.launch {
                when (scribble.status) {
                    ScribbleStatus.FAILED -> {
                        parsePendingScribblesUseCase(_state.value.currentDate)
                    }

                    ScribbleStatus.SUCCESS -> {
                        _state.update { it.copy(selectedScribble = scribble) }
                    }

                    else -> {}
                }
            }
        }

        private fun completeScribble(scribble: Scribble) {
            viewModelScope.launch {
                updateScribbleAsCompleteUseCase(scribble.id)
                dismissScribbleDialog()
            }
        }

        private fun deleteScribble(scribble: Scribble) {
            viewModelScope.launch {
                removeScribbleUseCase(scribble.id)
                dismissScribbleDialog()
            }
        }

        private fun dismissScribbleDialog() {
            _state.update { it.copy(selectedScribble = null) }
        }

        private fun editScribble(scribble: Scribble) {
            _state.update {
                it.copy(
                    currentScribbleText = scribble.rawText,
                    editingScribbleId = scribble.id,
                    selectedScribble = null,
                )
            }
        }
    }
