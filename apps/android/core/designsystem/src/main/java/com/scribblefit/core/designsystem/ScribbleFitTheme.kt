package com.scribblefit.core.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

data object ScribbleFitTheme {
    val colors: ScribbleFitColors
        @Composable @ReadOnlyComposable
        get() = LocalColors.current

    val typography: Typography
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: ScribbleFitShapes
        @Composable @ReadOnlyComposable
        get() = LocalShapes.current

    val spacing: ScribbleFitSpacing
        @Composable @ReadOnlyComposable
        get() = LocalSpacing.current
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScribbleFitTheme(
    isDynamicTheme: Boolean = false,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isDynamicTheme -> if (isSystemInDarkTheme) {
            dynamicLightColorScheme(LocalContext.current)
        } else {
            dynamicLightColorScheme(LocalContext.current)
        }

        else -> if (isSystemInDarkTheme) {
            darkScribbleFitColorScheme
        } else {
            lightScribbleFitColorScheme
        }
    }

    val scribbleFitColors = if (isSystemInDarkTheme) darkScribbleFitColors else lightScribbleFitColors

    CompositionLocalProvider(
        LocalColors provides scribbleFitColors,
        LocalShapes provides ScribbleFitShapes,
        LocalSpacing provides ScribbleFitSpacing
    ) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            shapes = ScribbleFitShapesTheme,
            content = content
        )
    }
}


private val lightScribbleFitColorScheme
    @Composable
    @ReadOnlyComposable
    get() = lightColorScheme(
        background = ScribbleFitTheme.colors.background,
        surface = ScribbleFitTheme.colors.background,
        onBackground = ScribbleFitTheme.colors.richBlack,
        onSurface = ScribbleFitTheme.colors.richBlack,
        primary = ScribbleFitTheme.colors.richBlack,
        onPrimary = ScribbleFitTheme.colors.background,
        error = ScribbleFitTheme.colors.errorText,
        errorContainer = ScribbleFitTheme.colors.errorBackground,
    )

private val darkScribbleFitColorScheme
    @Composable
    @ReadOnlyComposable
    get() = darkColorScheme(
        background = ScribbleFitTheme.colors.background,
        surface = ScribbleFitTheme.colors.background,
        onBackground = ScribbleFitTheme.colors.richBlack,
        onSurface = ScribbleFitTheme.colors.richBlack,
        primary = ScribbleFitTheme.colors.richBlack,
        onPrimary = ScribbleFitTheme.colors.background,
        error = ScribbleFitTheme.colors.errorText,
        errorContainer = ScribbleFitTheme.colors.errorBackground,
    )

private val ScribbleFitShapesTheme
    @Composable @ReadOnlyComposable
    get() = Shapes(
        small = RoundedCornerShape(ScribbleFitTheme.shapes.small),
        medium = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
        large = RoundedCornerShape(ScribbleFitTheme.shapes.large)
    )