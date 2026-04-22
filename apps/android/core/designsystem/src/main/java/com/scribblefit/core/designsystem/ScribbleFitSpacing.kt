package com.scribblefit.core.designsystem

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

data object ScribbleFitSpacing {
    val extraSmall = 4.dp
    val small = 8.dp
    val smallLarger = 12.dp
    val medium = 16.dp
    val mediumLarger = 18.dp
    val large = 24.dp
    val extraLarge = 32.dp
    val screenPadding = 24.dp
}

internal val LocalSpacing = staticCompositionLocalOf { ScribbleFitSpacing }
