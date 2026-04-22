package com.scribblefit.feature.settings.ui

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.settings.ui.components.AIConfigurationSection
import com.scribblefit.feature.settings.ui.components.AppearanceSection
import com.scribblefit.feature.settings.ui.components.DataManagementSection
import com.scribblefit.feature.settings.ui.components.UnitsSection

@Composable
internal fun SettingsScreen(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            SettingsTopBar(onBackClick = onBackClick)
        },
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
                    SettingsFooter(state = state, version = "2.4.0 (821)")
                }

                item {
                    Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.large))
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
