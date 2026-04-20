package com.scribblefit.feature.insights.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.BottomBar
import com.scribblefit.core.designsystem.BottomBarUiItem
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.SegmentedSelector
import com.scribblefit.core.designsystem.TopBar
import com.scribblefit.core.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InsightsScreen(
    state: InsightsState,
    onIntent: (InsightsIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(
                title = {
                    Text(
                        text = state.getTitle(),
                        style = ScribbleFitTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            )
        },
        containerColor = ScribbleFitTheme.colors.surface,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
            ) {
                SegmentedSelector(
                    options = listOf(
                        InsightsPeriod.DAILY to state.getDailyLabel(),
                        InsightsPeriod.WEEKLY to state.getWeeklyLabel(),
                        InsightsPeriod.MONTHLY to state.getMonthlyLabel(),
                    ),
                    selectedOption = state.selectedPeriod,
                    onOptionSelected = { onIntent(InsightsIntent.SelectPeriod(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = ScribbleFitTheme.spacing.screenPadding,
                            vertical = ScribbleFitTheme.spacing.small
                        )
                )

                Box(modifier = Modifier.weight(1f)) {
                    PullToRefreshBox(
                        isRefreshing = state.isLoading,
                        onRefresh = { onIntent(InsightsIntent.Refresh) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        InsightsBody(
                            state = state,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    val bottomBarItems = state.bottomBarState.items.map { item ->
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

                    BottomBar(
                        items = bottomBarItems,
                        selectedTab = state.bottomBarState.selectedTab,
                        isVisible = state.bottomBarState.isVisible,
                        onClick = { onIntent(InsightsIntent.NavigateToScreen(it)) },
                        modifier = Modifier
                            .padding(ScribbleFitTheme.spacing.medium)
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}
