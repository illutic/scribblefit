package com.scribblefit.feature.canvas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scribblefit.core.designsystem.BottomBarContainer
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.navigation.BottomBarState
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.canvas.ui.components.CanvasTopBar
import com.scribblefit.feature.canvas.ui.components.DateHeader
import com.scribblefit.feature.canvas.ui.components.Scribble
import com.scribblefit.feature.canvas.ui.components.ScribbleBottomSheet
import com.scribblefit.feature.canvas.ui.components.ScribbleInputPill

@Composable
fun CanvasRoute() {
    val viewModel = viewModel<CanvasViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    CanvasScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CanvasScreen(
    state: CanvasState,
    onIntent: (CanvasIntent) -> Unit,
) {
    if (state.selectedScribble != null) {
        ScribbleBottomSheet(
            scribble = state.selectedScribble,
            onConfirm = { onIntent(CanvasIntent.ConfirmScribble(state.selectedScribble)) },
            onDismiss = { onIntent(CanvasIntent.DismissScribbleDialog) },
            onDelete = { onIntent(CanvasIntent.DeleteScribble(state.selectedScribble)) },
            onEdit = { onIntent(CanvasIntent.UpdateScribble(state.selectedScribble)) },
        )
    }

    Scaffold(
        topBar = {
            Column {
                CanvasTopBar(
                    appName = state.appName,
                    onProfileClick = { onIntent(CanvasIntent.NavigateToScreen(Screen.Profile)) },
                )
                DateHeader(
                    currentDate = state.dateString,
                    isCurrentDate = state.isCurrentDate,
                    onPreviousDayClick = { onIntent(CanvasIntent.OnPreviousDayClick) },
                    onNextDayClick = { onIntent(CanvasIntent.OnNextDayClick) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        },
        bottomBar = {
            Column {
                ScribbleInputPill(
                    value = state.currentScribbleText,
                    placeholder = state.textfieldPlaceholder,
                    onValueChange = { text -> onIntent(CanvasIntent.UpdateScribbleText(text)) },
                    onSendClick = { onIntent(CanvasIntent.AddScribble(state.currentScribbleText)) },
                    isLoading = state.isLoading,
                    modifier = Modifier.imePadding(),
                )

                BottomBarContainer(
                    bottomBarState = state.bottomBarState,
                    onClick = { screen -> onIntent(CanvasIntent.NavigateToScreen(screen)) },
                )
            }
        },
        containerColor = ScribbleFitTheme.colors.background,
        contentColor = ScribbleFitTheme.colors.richBlack,
    ) { padding ->
        if (state.scribbles.isEmpty()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.emptyScribbleText,
                    style = ScribbleFitTheme.typography.bodyLarge,
                    color = ScribbleFitTheme.colors.midGray,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                contentPadding = PaddingValues(ScribbleFitTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium),
            ) {
                items(state.scribbles) { scribble ->
                    Scribble(
                        scribble = scribble,
                        onClick = { onIntent(CanvasIntent.ClickOnScribble(scribble)) },
                    )
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun CanvasScreenPreview() {
    ScribbleFitTheme {
        CanvasScreen(
            state =
                CanvasState(
                    bottomBarState = BottomBarState(isVisible = true, selectedTab = Screen.Canvas),
                ),
            onIntent = {},
        )
    }
}
