package com.scribblefit.feature.insights.ui

import com.scribblefit.core.navigation.Screen

sealed interface InsightsIntent {
    data object Refresh : InsightsIntent
    data class NavigateToScreen(val screen: Screen) : InsightsIntent
}
