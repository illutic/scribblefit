package com.scribblefit.core.designsystem

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

data object ScribbleFitSpacing {
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val screenPadding = 24.dp
}

internal val LocalSpacing = staticCompositionLocalOf { ScribbleFitSpacing }
