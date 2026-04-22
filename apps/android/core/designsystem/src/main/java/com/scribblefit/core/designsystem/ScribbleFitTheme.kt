package com.scribblefit.core.designsystem

import android.app.Activity
import android.app.LocalActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineExceptionHandler

private val InterFontFamily = FontFamily(
    Font(
        R.font.inter_regular
    ),
    Font(
        R.font.inter_italic
    )
)

private val ScribbleFitTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp, // ~3.5rem
        letterSpacing = (-0.04).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp, // ~1.5rem
        letterSpacing = (-0.02).sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp, // ~1.125rem
        letterSpacing = (-0.01).sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp, // ~0.875rem
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, // ~0.75rem
        letterSpacing = 0.05.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 0.1.sp
    )
)

data object ScribbleFitTheme {
    val colors: ScribbleFitColors
        @Composable @ReadOnlyComposable
        get() = LocalColors.current

    val typography: Typography
        @Composable @ReadOnlyComposable
        get() = ScribbleFitTypography

    val shapes: ScribbleFitShapes
        @Composable @ReadOnlyComposable
        get() = LocalShapes.current

    val spacing: ScribbleFitSpacing
        @Composable @ReadOnlyComposable
        get() = LocalSpacing.current

    object Alphas {
        const val glassBackground = 0.5f
        const val secondaryText = 0.7f
        const val cardOverlay = 0.03f
        const val badgeBackground = 0.1f
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScribbleFitTheme(
    isDynamicTheme: Boolean = false,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val scribbleFitColors =
        if (isSystemInDarkTheme) darkScribbleFitColors else lightScribbleFitColors

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isDynamicTheme -> {
            val context = LocalContext.current
            if (isSystemInDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> if (isSystemInDarkTheme) {
            darkScribbleFitColorScheme(scribbleFitColors)
        } else {
            lightScribbleFitColorScheme(scribbleFitColors)
        }
    }

    val localView = LocalView.current

    LaunchedEffect(localView, isSystemInDarkTheme) {
        val activity = localView.context.findActivity()
        val window = activity?.window ?: return@LaunchedEffect
        val decorView = window.decorView
        val windowInsetController = WindowCompat.getInsetsController(window, decorView)
        windowInsetController.isAppearanceLightStatusBars = !isSystemInDarkTheme
        windowInsetController.isAppearanceLightNavigationBars = !isSystemInDarkTheme
    }

    CompositionLocalProvider(
        LocalColors provides scribbleFitColors,
        LocalShapes provides ScribbleFitShapes,
        LocalSpacing provides ScribbleFitSpacing,
        LocalContentColor provides colorScheme.primary,
    ) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            shapes = ScribbleFitShapesTheme,
            content = content
        )
    }
}

private fun lightScribbleFitColorScheme(colors: ScribbleFitColors) = lightColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    surface = colors.surface,
    onSurface = colors.primary,
    background = colors.surface,
    onBackground = colors.primary,
    error = colors.dangerRed,
)

private fun darkScribbleFitColorScheme(colors: ScribbleFitColors) = darkColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    surface = colors.surface,
    onSurface = colors.primary,
    background = colors.surface,
    onBackground = colors.primary,
    error = colors.dangerRed,
)

private val ScribbleFitShapesTheme
    @Composable @ReadOnlyComposable
    get() = Shapes(
        small = RoundedCornerShape(ScribbleFitTheme.shapes.small),
        medium = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
        large = RoundedCornerShape(ScribbleFitTheme.shapes.large)
    )

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}