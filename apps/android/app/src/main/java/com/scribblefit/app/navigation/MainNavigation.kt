package com.scribblefit.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.scribblefit.core.navigation.NavState
import com.scribblefit.core.navigation.Screen

@Composable
fun MainNavigation(
    navState: NavState,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = navState.backStack,
        modifier = modifier
    ) { screen ->
        when (screen) {
            Screen.Canvas -> NavEntry(screen) {

            }

            Screen.Insights -> NavEntry(screen) {

            }

            Screen.Ledger -> NavEntry(screen) {

            }

            Screen.Profile -> NavEntry(screen) {

            }
        }
    }
}
