package com.scribblefit.core.designsystem

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

data object ScribbleFitShapes {
    val small = 8.dp
    val smallLarger = 10.dp
    val medium = 12.dp
    val large = 20.dp
}

internal val LocalShapes = staticCompositionLocalOf { ScribbleFitShapes }
