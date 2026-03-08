package com.scribblefit.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ScribbleFitColorScheme = lightColorScheme(
    background = ScribbleFitColors.Background,
    surface = ScribbleFitColors.Background,
    onBackground = ScribbleFitColors.RichBlack,
    onSurface = ScribbleFitColors.RichBlack,
    primary = ScribbleFitColors.RichBlack,
    onPrimary = ScribbleFitColors.Background,
    error = ScribbleFitColors.DangerRed
)

private val ScribbleFitShapesTheme = Shapes(
    small = RoundedCornerShape(ScribbleFitShapes.Small),
    medium = RoundedCornerShape(ScribbleFitShapes.Medium),
    large = RoundedCornerShape(ScribbleFitShapes.Large)
)

@Composable
fun ScribbleFitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ScribbleFitColorScheme,
        shapes = ScribbleFitShapesTheme,
        content = content
    )
}
