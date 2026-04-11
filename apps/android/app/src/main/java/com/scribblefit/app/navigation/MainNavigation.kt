package com.scribblefit.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.scribblefit.core.navigation.NavState
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.canvas.ui.CanvasRoute
import com.scribblefit.feature.insights.ui.InsightsRoute
import com.scribblefit.feature.settings.ui.SettingsRoute

@Composable
fun MainNavigation(
    navState: NavState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = navState.backStack,
        onBack = onBack,
        modifier = modifier,
    ) { screen ->
        when (screen) {
            Screen.Canvas -> {
                NavEntry(screen) { CanvasRoute() }
            }

            Screen.Insights -> {
                NavEntry(screen) { InsightsRoute() }
            }

            Screen.Ledger -> {
                NavEntry(screen) {
                }
            }

            Screen.Profile -> {
                NavEntry(screen) {
                }
            }

            Screen.Settings -> {
                NavEntry(screen) {
                    SettingsRoute(onBackClick = onBack)
                }
            }
        }
    }
}
