package com.scribblefit.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.navigation.NavState
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.canvas.ui.CanvasRoute
import com.scribblefit.feature.exercises.ui.ExerciseDetailsRoute
import com.scribblefit.feature.exercises.ui.ExerciseTrendsRoute
import com.scribblefit.feature.insights.ui.InsightsRoute
import com.scribblefit.feature.ledger.ui.LedgerRoute
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
        modifier = modifier.background(ScribbleFitTheme.colors.background),
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        popTransitionSpec = { fadeIn() togetherWith fadeOut() },
        predictivePopTransitionSpec = {
            slideInHorizontally { it / 2 } togetherWith
                    slideOutHorizontally() + fadeOut()
        }
    ) { screen ->
        when (screen) {
            Screen.Canvas -> {
                NavEntry(screen) { CanvasRoute() }
            }

            Screen.Insights -> {
                NavEntry(screen) { InsightsRoute() }
            }

            Screen.Ledger -> {
                NavEntry(screen) { LedgerRoute() }
            }

            Screen.Settings -> {
                NavEntry(screen) {
                    SettingsRoute(onBackClick = onBack)
                }
            }

            is Screen.ExerciseDetails -> {
                NavEntry(screen) {
                    ExerciseDetailsRoute(exerciseId = screen.exerciseId)
                }
            }

            is Screen.ExerciseTrends -> {
                NavEntry(screen) {
                    ExerciseTrendsRoute()
                }
            }
        }
    }
}
