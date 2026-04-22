package com.scribblefit.feature.ledger.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.BottomBar
import com.scribblefit.core.designsystem.BottomBarUiItem
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.ledger.ui.components.DatePickerDialog
import com.scribblefit.feature.ledger.ui.components.DateRangePickerButton
import com.scribblefit.feature.ledger.ui.components.EmptyLedgerContent
import com.scribblefit.feature.ledger.ui.components.LedgerContent
import com.scribblefit.feature.ledger.ui.components.LedgerHeader
import com.scribblefit.feature.ledger.ui.components.LedgerLoadingContent

@Composable
internal fun LedgerScreen(
    viewModel: LedgerViewModel
) {
    val state by viewModel.state.collectAsState()
    val onIntent by rememberUpdatedState(viewModel::onIntent)

    DatePickerDialog(
        state = state,
        onIntent = onIntent
    )

    Scaffold(
        topBar = { 
            LedgerHeader(
                title = state.ledgerTitle,
                onRefreshClick = { onIntent(LedgerIntent.Refresh) }
            ) 
        },
        containerColor = ScribbleFitTheme.colors.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
            ) {
                DateRangePickerButton(
                    modifier = Modifier.padding(
                        horizontal = ScribbleFitTheme.spacing.screenPadding,
                        vertical = ScribbleFitTheme.spacing.small
                    ),
                    dateRange = state.dateRangeString,
                    onDateRangeClick = { onIntent(LedgerIntent.ShowDatePicker) }
                )

                Box(modifier = Modifier.weight(1f)) {
                    when {
                        state.isLoading -> {
                            LedgerLoadingContent(modifier = Modifier.fillMaxSize())
                        }

                        state.groupedWorkouts.isEmpty() -> {
                            EmptyLedgerContent(
                                modifier = Modifier.fillMaxSize(),
                                title = state.emptyTitle,
                                cta = state.emptyCta,
                                onCtaClick = { onIntent(LedgerIntent.NavigateToScreen(Screen.Canvas)) }
                            )
                        }

                        else -> {
                            LedgerContent(
                                state = state,
                                onIntent = onIntent,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    val bottomBarItems = state.bottomBarState.items.map { item ->
                        val icon = when (item.screen) {
                            Screen.Canvas -> Icons.Rounded.Home
                            Screen.Insights -> Icons.Rounded.AutoGraph
                            Screen.Ledger -> Icons.Rounded.CalendarMonth
                            Screen.Settings -> Icons.Rounded.Settings
                            else -> return@Scaffold
                        }
                        val label = when (item.screen) {
                            Screen.Canvas -> stringResource(R.string.nav_canvas)
                            Screen.Insights -> stringResource(R.string.nav_insights)
                            Screen.Ledger -> stringResource(R.string.nav_ledger)
                            Screen.Settings -> stringResource(R.string.nav_settings)
                            else -> return@Scaffold
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
                        onClick = { onIntent(LedgerIntent.NavigateToScreen(it)) },
                        modifier = Modifier
                            .padding(ScribbleFitTheme.spacing.medium)
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}
