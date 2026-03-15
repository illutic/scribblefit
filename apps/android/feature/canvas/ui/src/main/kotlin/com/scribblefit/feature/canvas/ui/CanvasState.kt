package com.scribblefit.feature.canvas.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.intl.Locale
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.navigation.BottomBarState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter =
    DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.current.platformLocale)

data class CanvasState(
    val isLoading: Boolean = false,
    val currentDate: LocalDate = LocalDate.now(),
    val error: Throwable? = null,
    val currentScribbleText: String = "",
    val scribbles: List<Scribble> = emptyList(),
    val selectedScribble: Scribble? = null,
    val bottomBarState: BottomBarState = BottomBarState()
) {
    val isCurrentDate = currentDate == LocalDate.now()
    val dateString: String by lazy { currentDate.format(dateFormatter) }

    // TODO - Add strings to resources

    val emptyScribbleText: String
        @Composable @ReadOnlyComposable get() = "Start scribbling.\n Type your first set below."

    val errorMessage: String
        @Composable @ReadOnlyComposable get() = error?.localizedMessage
            ?: "An unknown error occurred."

    val textfieldPlaceholder: String
        @Composable @ReadOnlyComposable get() = "What did you lift today?"

    val appName: String
        @Composable @ReadOnlyComposable get() = "ScribbleFit"
}
