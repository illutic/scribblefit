package com.scribblefit.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import com.scribblefit.app.R
import com.scribblefit.core.designsystem.BottomBarUiItem
import com.scribblefit.core.navigation.BottomBarState
import com.scribblefit.core.navigation.Screen

@Composable
fun BottomBarState.toUiItems(): List<BottomBarUiItem> {
    return items.map { item ->
        val icon = when (item.screen) {
            Screen.Canvas -> Icons.Rounded.Home
            Screen.Insights -> Icons.Rounded.AutoGraph
            Screen.Ledger -> Icons.Rounded.CalendarMonth
            Screen.Settings -> Icons.Rounded.Settings
        }
        val label = when (item.screen) {
            Screen.Canvas -> stringResource(R.string.nav_canvas)
            Screen.Insights -> stringResource(R.string.nav_insights)
            Screen.Ledger -> stringResource(R.string.nav_ledger)
            Screen.Settings -> stringResource(R.string.nav_settings)
        }
        BottomBarUiItem(
            screen = item.screen,
            icon = rememberVectorPainter(icon),
            label = label
        )
    }
}
