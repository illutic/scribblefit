package com.scribblefit.core.designsystem

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val BackgroundColor = Color(0xFFFFFFFF)
private val DarkBackgroundColor = Color(0xFF000000)

private val SoftGrayColor = Color(0xFFF7F7F8)
private val DarkSoftGrayColor = Color(0xFF1A1A1A)

private val RichBlackColor = Color(0xFF101010)
private val DarkRichBlackColor = Color(0xFFFFFFFF)

private val MidGrayColor = Color(0xFF8E8EA0)
private val DarkMidGrayColor = Color(0xFF8E8EA0)

private val LightGrayColor = Color(0xFFE5E5EA)
private val DarkLightGrayColor = Color(0xFF2C2C2E)

private val DangerRedColor = Color(0xFFFF3B30)
private val DarkDangerRedColor = Color(0xFFFF453A)

private val ErrorBackgroundColor = Color(0xFFFEE2E2)
private val DarkErrorBackgroundColor = Color(0xFF5A1E1E)

private val ErrorTextColor = Color(0xFF991B1B)
private val DarkErrorTextColor = Color(0xFFFFB3B3)

private val SuccessGreenColor = Color(0xFF34C759)
private val DarkSuccessGreenColor = Color(0xFF30D158)

private val SuccessGreenBackgroundColor = Color(0xFFE6F4EA)
private val DarkSuccessGreenBackgroundColor = Color(0xFF1C3E1C)

data class ScribbleFitColors(
    val background: Color = BackgroundColor,
    val softGray: Color = SoftGrayColor,
    val richBlack: Color = RichBlackColor,
    val midGray: Color = MidGrayColor,
    val lightGray: Color = LightGrayColor,
    val dangerRed: Color = DangerRedColor,
    val errorBackground: Color = ErrorBackgroundColor,
    val errorText: Color = ErrorTextColor,
    val successGreen: Color = SuccessGreenColor,
    val successGreenBackground: Color = SuccessGreenBackgroundColor
)

internal val lightScribbleFitColors = ScribbleFitColors()

internal val darkScribbleFitColors = ScribbleFitColors(
    background = DarkBackgroundColor,
    softGray = DarkSoftGrayColor,
    richBlack = DarkRichBlackColor,
    midGray = DarkMidGrayColor,
    lightGray = DarkLightGrayColor,
    dangerRed = DarkDangerRedColor,
    errorBackground = DarkErrorBackgroundColor,
    errorText = DarkErrorTextColor,
    successGreen = DarkSuccessGreenColor,
    successGreenBackground = DarkSuccessGreenBackgroundColor
)

internal val LocalColors = staticCompositionLocalOf { lightScribbleFitColors }

