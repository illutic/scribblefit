package com.scribblefit.feature.canvas.ui

import com.scribblefit.core.model.Scribble
import com.scribblefit.core.navigation.Screen

sealed interface CanvasIntent {
    // Canvas
    data class UpdateScribbleText(
        val text: String,
    ) : CanvasIntent

    data class AddScribble(
        val scribble: String,
    ) : CanvasIntent

    data class ClickOnScribble(
        val scribble: Scribble,
    ) : CanvasIntent

    data object OnPreviousDayClick : CanvasIntent

    data object OnNextDayClick : CanvasIntent

    // Scribble Dialog
    data class UpdateScribble(
        val scribble: Scribble,
    ) : CanvasIntent

    data class DeleteScribble(
        val scribble: Scribble,
    ) : CanvasIntent

    data class ConfirmScribble(
        val scribble: Scribble,
    ) : CanvasIntent

    data object DismissScribbleDialog : CanvasIntent

    // Navigation
    data object NavigateBack : CanvasIntent

    data class NavigateToScreen(
        val screen: Screen,
    ) : CanvasIntent
}
