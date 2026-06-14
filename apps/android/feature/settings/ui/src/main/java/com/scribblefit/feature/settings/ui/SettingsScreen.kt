package com.scribblefit.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.settings.ui.components.AIConfigurationSection
import com.scribblefit.feature.settings.ui.components.AppearanceSection
import com.scribblefit.feature.settings.ui.components.DataManagementSection
import com.scribblefit.feature.settings.ui.components.UnitsSection
import kotlinx.coroutines.launch

@Composable
internal fun SettingsScreen(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    onBackClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { msg ->
            onIntent(SettingsIntent.DismissError)
            scope.launch {
                snackbarHostState.showSnackbar(msg)
            }
        }
    }

    Scaffold(
        topBar = {
            SettingsTopBar(onBackClick = onBackClick)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ScribbleFitTheme.colors.surfaceContainerLowest
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxHeight()
                    .imePadding(),
                contentPadding = PaddingValues(ScribbleFitTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large)
            ) {
                item {
                    AppearanceSection(
                        state = state,
                        onThemeChange = { onIntent(SettingsIntent.ChangeTheme(it)) },
                        onDynamicThemeToggle = { onIntent(SettingsIntent.ToggleDynamicTheme(it)) }
                    )
                }

                item {
                    AIConfigurationSection(
                        state = state,
                        onIntent = onIntent
                    )
                }

                item {
                    UnitsSection(
                        state = state,
                        onUnitChange = { onIntent(SettingsIntent.ChangeWeightUnit(it)) }
                    )
                }

                item {
                    DataManagementSection(
                        state = state,
                        onExport = { onIntent(SettingsIntent.ExportData) },
                        onClearData = { onIntent(SettingsIntent.ShowClearDataDialog) }
                    )
                }

                item {
                    SettingsFooter(state = state)
                }

                item {
                    Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.large))
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ScribbleFitTheme.colors.surface.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = ScribbleFitTheme.colors.primary
                    )
                }
            }
        }
    }

    if (state.showClearDataDialog) {
        ClearDataConfirmationDialog(
            onConfirm = { onIntent(SettingsIntent.ClearAllData) },
            onDismiss = { onIntent(SettingsIntent.DismissClearDataDialog) }
        )
    }
}
