package com.scribblefit.core.designsystem

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val PrimaryColor = Color(0xFF000000)
private val OnPrimaryColor = Color(0xFFE5E2E1)
private val DarkPrimaryColor = Color(0xFFFFFFFF)
private val DarkOnPrimaryColor = Color(0xFF000000)

private val SurfaceColor = Color(0xFFF9F9F9)
private val DarkSurfaceColor = Color(0xFF101010)

private val SurfaceContainerLowestColor = Color(0xFFFFFFFF)
private val DarkSurfaceContainerLowestColor = Color(0xFF1A1A1A)

private val SurfaceContainerLowColor = Color(0xFFF3F3F4)
private val DarkSurfaceContainerLowColor = Color(0xFF242424)

private val SurfaceContainerColor = Color(0xFFEEEEEE)
private val DarkSurfaceContainerColor = Color(0xFF2C2C2E)

private val SurfaceContainerHighColor = Color(0xFFE8E8E8)
private val DarkSurfaceContainerHighColor = Color(0xFF3C3C3E)

private val MidGrayColor = Color(0xFF8E8EA0)
private val DarkMidGrayColor = Color(0xFF8E8EA0)

private val DangerRedColor = Color(0xFFFF3B30)
private val DarkDangerRedColor = Color(0xFFFF453A)

private val WarningOrangeColor = Color(0xFFFF9500)
private val DarkWarningOrangeColor = Color(0xFFFF9F0A)

private val SuccessGreenColor = Color(0xFF34C759)
private val DarkSuccessGreenColor = Color(0xFF30D158)

private val OutlineVariantColor = Color(0xFFC6C6C6)
private val DarkOutlineVariantColor = Color(0xFF444446)

data class ScribbleFitColors(
    val primary: Color = PrimaryColor,
    val onPrimary: Color = OnPrimaryColor,
    val surface: Color = SurfaceColor,
    val surfaceContainerLowest: Color = SurfaceContainerLowestColor,
    val surfaceContainerLow: Color = SurfaceContainerLowColor,
    val surfaceContainer: Color = SurfaceContainerColor,
    val surfaceContainerHigh: Color = SurfaceContainerHighColor,
    val midGray: Color = MidGrayColor,
    val dangerRed: Color = DangerRedColor,
    val warningOrange: Color = WarningOrangeColor,
    val successGreen: Color = SuccessGreenColor,
    val outlineVariant: Color = OutlineVariantColor,
    val background: Color = surface,
)

internal val lightScribbleFitColors = ScribbleFitColors()

internal val darkScribbleFitColors = ScribbleFitColors(
    primary = DarkPrimaryColor,
    onPrimary = DarkOnPrimaryColor,
    surface = DarkSurfaceColor,
    surfaceContainerLowest = DarkSurfaceContainerLowestColor,
    surfaceContainerLow = DarkSurfaceContainerLowColor,
    surfaceContainer = DarkSurfaceContainerColor,
    surfaceContainerHigh = DarkSurfaceContainerHighColor,
    midGray = DarkMidGrayColor,
    dangerRed = DarkDangerRedColor,
    warningOrange = DarkWarningOrangeColor,
    successGreen = DarkSuccessGreenColor,
    outlineVariant = DarkOutlineVariantColor
)

internal val LocalColors = staticCompositionLocalOf { lightScribbleFitColors }

