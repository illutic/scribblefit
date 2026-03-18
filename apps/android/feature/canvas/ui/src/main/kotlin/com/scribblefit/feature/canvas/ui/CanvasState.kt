package com.scribblefit.feature.canvas.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
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
    val editingScribbleId: Long? = null,
    val scribbles: List<Scribble> = emptyList(),
    val selectedScribble: Scribble? = null,
    val bottomBarState: BottomBarState = BottomBarState(),
) {
    val isCurrentDate = currentDate == LocalDate.now()
    val dateString: String by lazy { currentDate.format(dateFormatter) }

    val emptyScribbleText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_empty_scribble_text)

    val errorMessage: String
        @Composable @ReadOnlyComposable
        get() =
            error?.localizedMessage
                ?: stringResource(R.string.canvas_error_unknown)

    val textfieldPlaceholder: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_textfield_placeholder)

    val appName: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_app_name)
}
