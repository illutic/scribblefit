package com.scribblefit.feature.insights.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.BottomBar
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

                BottomBar(
                    bottomBarState = state.bottomBarState,
                    onClick = { onIntent(InsightsIntent.NavigateToScreen(it)) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}
