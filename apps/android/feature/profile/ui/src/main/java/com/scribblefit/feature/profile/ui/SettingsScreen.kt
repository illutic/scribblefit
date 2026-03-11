package com.scribblefit.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.designsystem.ScribbleFitColors
import com.scribblefit.core.designsystem.ScribbleFitSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    uiState: SettingsUiState,
    onProviderChanged: (LLMProvider) -> Unit,
    onModelSelected: (String) -> Unit,
    onApiKeySaved: (String) -> Unit,
    onWeightUnitChanged: (Weight) -> Unit,
    onThemeChanged: (ThemePreference) -> Unit,
    onClearDataTapped: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showProviderSheet by remember { mutableStateOf(false) }
    var showModelSheet by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showApiKeyInput by remember { mutableStateOf(uiState.showApiKeyInput) }
    var apiKeyInputText by remember { mutableStateOf("") }

    val needsApiKey = uiState.settings.preferredLlmProvider.requiresApiKey

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScribbleFitColors.Background)
    ) {
        Text(
            text = "Settings",
            fontSize = TITLE_FONT_SIZE_SP.sp,
            fontWeight = FontWeight.SemiBold,
            color = ScribbleFitColors.RichBlack,
            modifier = Modifier.padding(
                start = ScribbleFitSpacing.Medium,
                end = ScribbleFitSpacing.Medium,
                top = TITLE_TOP_MARGIN_DP.dp
            )
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // AI ENGINE
            item {
                Spacer(modifier = Modifier.height(FIRST_SECTION_TOP_DP.dp))
                SectionHeader(title = "AI ENGINE")
            }
            item {
                SettingsRow(
                    label = "Provider",
                    value = uiState.settings.preferredLlmProvider.displayName(),
                    onClick = { showProviderSheet = true }
                )
            }
            if (needsApiKey) {
                item {
                    SettingsRow(
                        label = "Model",
                        value = uiState.settings.preferredModel?.ifBlank { "Auto" } ?: "Auto",
                        onClick = {
                            if (uiState.availableModels.isNotEmpty()) showModelSheet = true
                        }
                    )
                }
                item {
                    ApiKeyRow(
                        showInput = showApiKeyInput,
                        inputText = apiKeyInputText,
                        onInputTextChanged = { apiKeyInputText = it },
                        onPillClicked = { showApiKeyInput = !showApiKeyInput },
                        onConfirm = {
                            onApiKeySaved(apiKeyInputText)
                            apiKeyInputText = ""
                            showApiKeyInput = false
                        }
                    )
                }
            }

            // PREFERENCES
            item {
                Spacer(modifier = Modifier.height(SUBSEQUENT_SECTION_TOP_DP.dp))
                SectionHeader(title = "PREFERENCES")
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = ROW_HORIZONTAL_PADDING_DP.dp,
                            vertical = ROW_VERTICAL_PADDING_DP.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weight Unit",
                        fontSize = ROW_LABEL_FONT_SIZE_SP.sp,
                        color = ScribbleFitColors.RichBlack
                    )
                    WeightUnitToggle(
                        selected = uiState.settings.weightUnit,
                        onUnitSelected = onWeightUnitChanged
                    )
                }
                RowDivider()
            }
            item {
                SettingsRow(
                    label = "Theme",
                    value = uiState.settings.themePreference.name.replaceFirstChar { it.uppercase() },
                    onClick = { showThemeSheet = true }
                )
            }

            // DATA
            item {
                Spacer(modifier = Modifier.height(SUBSEQUENT_SECTION_TOP_DP.dp))
                SectionHeader(title = "DATA")
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showClearDataDialog = true }
                        .padding(
                            horizontal = ROW_HORIZONTAL_PADDING_DP.dp,
                            vertical = ROW_VERTICAL_PADDING_DP.dp
                        )
                ) {
                    Text(
                        text = "Clear All Data",
                        fontSize = ROW_LABEL_FONT_SIZE_SP.sp,
                        color = ScribbleFitColors.DangerRed
                    )
                }
                RowDivider()
            }
        }
    }

    if (showProviderSheet) {
        ProviderBottomSheet(
            currentProvider = uiState.settings.preferredLlmProvider,
            onProviderSelected = { provider ->
                onProviderChanged(provider)
                showProviderSheet = false
                showApiKeyInput = provider != LLMProvider.LOCAL
            },
            onDismiss = { showProviderSheet = false }
        )
    }

    if (showModelSheet) {
        ModelBottomSheet(
            models = uiState.availableModels,
            onModelSelected = { model ->
                onModelSelected(model)
                showModelSheet = false
            },
            onDismiss = { showModelSheet = false }
        )
    }

    if (showThemeSheet) {
        ThemeBottomSheet(
            currentTheme = uiState.settings.themePreference,
            onThemeSelected = { theme ->
                onThemeChanged(theme)
                showThemeSheet = false
            },
            onDismiss = { showThemeSheet = false }
        )
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text(text = "Clear All Data") },
            text = { Text(text = "This will permanently delete all your workout data and settings. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    onClearDataTapped()
                    showClearDataDialog = false
                }) {
                    Text(text = "Clear", color = ScribbleFitColors.DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}
