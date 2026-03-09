package com.scribblefit.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scribblefit.core.designsystem.ScribbleFitColors
import com.scribblefit.core.designsystem.ScribbleFitSpacing
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.profile.domain.model.ThemePreference
import com.scribblefit.feature.profile.domain.model.WeightUnit

private const val TITLE_FONT_SIZE_SP = 28
private const val SECTION_HEADER_FONT_SIZE_SP = 12
private const val ROW_LABEL_FONT_SIZE_SP = 17
private const val ROW_VALUE_FONT_SIZE_SP = 15
private const val SEGMENT_FONT_SIZE_SP = 13
private const val PILL_FONT_SIZE_SP = 15

private const val TITLE_TOP_MARGIN_DP = 24
private const val FIRST_SECTION_TOP_DP = 24
private const val SUBSEQUENT_SECTION_TOP_DP = 32
private const val SECTION_HORIZONTAL_MARGIN_DP = 16
private const val ROW_HORIZONTAL_PADDING_DP = 16
private const val ROW_VERTICAL_PADDING_DP = 16
private const val DIVIDER_HEIGHT_DP = 1
private const val SEGMENT_CORNER_DP = 6
private const val PILL_CORNER_DP = 8
private const val PILL_BORDER_DP = 1
private const val PILL_HORIZONTAL_PADDING_DP = 12
private const val PILL_VERTICAL_PADDING_DP = 6
private const val API_KEY_LAST_CHARS = 4

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onProviderChanged = viewModel::onProviderChanged,
        onModelSelected = viewModel::onModelSelected,
        onApiKeySaved = viewModel::onApiKeySaved,
        onWeightUnitChanged = viewModel::onWeightUnitChanged,
        onThemeChanged = viewModel::onThemeChanged,
        onClearDataTapped = viewModel::onClearDataTapped,
        modifier = modifier
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onProviderChanged: (LLMProvider) -> Unit,
    onModelSelected: (String) -> Unit,
    onApiKeySaved: (String) -> Unit,
    onWeightUnitChanged: (WeightUnit) -> Unit,
    onThemeChanged: (ThemePreference) -> Unit,
    onClearDataTapped: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showProviderDialog by remember { mutableStateOf(false) }
    var showModelDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showApiKeyInput by remember { mutableStateOf(uiState.showApiKeyInput) }
    var apiKeyInputText by remember { mutableStateOf("") }

    val needsApiKey = uiState.settings.aiProvider != LLMProvider.PROXY && uiState.settings.aiProvider != LLMProvider.LOCAL

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
            item {
                Spacer(modifier = Modifier.height(FIRST_SECTION_TOP_DP.dp))
                SectionHeader(title = "AI ENGINE")
            }

            item {
                SettingsRow(
                    label = "Provider",
                    value = uiState.settings.aiProvider.rawValue.replaceFirstChar { it.uppercase() },
                    onClick = { showProviderDialog = true }
                )
            }

            if (needsApiKey) {
                item {
                    val modelDisplay = uiState.settings.selectedModel.ifBlank { "Auto" }
                    SettingsRow(
                        label = "Model",
                        value = modelDisplay,
                        onClick = {
                            if (uiState.availableModels.isNotEmpty()) {
                                showModelDialog = true
                            }
                        }
                    )
                }

                item {
                    ApiKeyRow(
                        apiKey = uiState.apiKey,
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
                    onClick = { showThemeDialog = true }
                )
            }

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

    if (showProviderDialog) {
        ProviderPickerDialog(
            currentProvider = uiState.settings.aiProvider,
            onProviderSelected = { provider ->
                onProviderChanged(provider)
                showProviderDialog = false
                showApiKeyInput = provider != LLMProvider.PROXY
            },
            onDismiss = { showProviderDialog = false }
        )
    }

    if (showModelDialog) {
        ModelPickerDialog(
            models = uiState.availableModels,
            onModelSelected = { model ->
                onModelSelected(model)
                showModelDialog = false
            },
            onDismiss = { showModelDialog = false }
        )
    }

    if (showThemeDialog) {
        ThemePickerDialog(
            currentTheme = uiState.settings.themePreference,
            onThemeSelected = { theme ->
                onThemeChanged(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
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

@Composable
private fun SectionHeader(title: String) {
    Column {
        Text(
            text = title,
            fontSize = SECTION_HEADER_FONT_SIZE_SP.sp,
            fontWeight = FontWeight.SemiBold,
            color = ScribbleFitColors.MidGray,
            modifier = Modifier.padding(
                start = SECTION_HORIZONTAL_MARGIN_DP.dp,
                end = SECTION_HORIZONTAL_MARGIN_DP.dp,
                bottom = ScribbleFitSpacing.Small
            )
        )
        RowDivider()
    }
}

@Composable
private fun SettingsRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = ROW_HORIZONTAL_PADDING_DP.dp,
                vertical = ROW_VERTICAL_PADDING_DP.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = ROW_LABEL_FONT_SIZE_SP.sp,
            color = ScribbleFitColors.RichBlack
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = ROW_VALUE_FONT_SIZE_SP.sp,
                color = ScribbleFitColors.MidGray
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = ScribbleFitColors.MidGray
            )
        }
    }
    RowDivider()
}

@Composable
private fun ApiKeyRow(
    apiKey: String,
    showInput: Boolean,
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onPillClicked: () -> Unit,
    onConfirm: () -> Unit
) {
    Column {
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
                text = "API Key",
                fontSize = ROW_LABEL_FONT_SIZE_SP.sp,
                color = ScribbleFitColors.RichBlack
            )
            Spacer(modifier = Modifier.weight(1f))
            val maskedDisplay = if (apiKey.length >= API_KEY_LAST_CHARS) {
                "\u2022\u2022\u2022\u2022" + apiKey.takeLast(API_KEY_LAST_CHARS)
            } else {
                "Not set"
            }
            Text(
                text = maskedDisplay,
                fontSize = ROW_VALUE_FONT_SIZE_SP.sp,
                color = ScribbleFitColors.MidGray,
                modifier = Modifier.padding(end = ScribbleFitSpacing.Small)
            )
            SaveKeyPill(onClick = onPillClicked)
        }
        if (showInput) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChanged,
                placeholder = { Text(text = "Paste your API key") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onConfirm() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ROW_HORIZONTAL_PADDING_DP.dp)
                    .padding(bottom = ROW_VERTICAL_PADDING_DP.dp)
            )
        }
        RowDivider()
    }
}

@Composable
private fun SaveKeyPill(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(PILL_CORNER_DP.dp))
            .background(ScribbleFitColors.SoftGray)
            .border(
                width = PILL_BORDER_DP.dp,
                color = ScribbleFitColors.LightGray,
                shape = RoundedCornerShape(PILL_CORNER_DP.dp)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = PILL_HORIZONTAL_PADDING_DP.dp,
                vertical = PILL_VERTICAL_PADDING_DP.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Save Key",
            fontSize = PILL_FONT_SIZE_SP.sp,
            fontWeight = FontWeight.SemiBold,
            color = ScribbleFitColors.RichBlack
        )
    }
}

@Composable
private fun WeightUnitToggle(
    selected: WeightUnit,
    onUnitSelected: (WeightUnit) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(SEGMENT_CORNER_DP.dp))
            .background(ScribbleFitColors.SoftGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WeightUnit.entries.forEach { unit ->
            val isActive = unit == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(SEGMENT_CORNER_DP.dp))
                    .background(if (isActive) ScribbleFitColors.RichBlack else Color.Transparent)
                    .clickable { onUnitSelected(unit) }
                    .padding(horizontal = PILL_HORIZONTAL_PADDING_DP.dp, vertical = PILL_VERTICAL_PADDING_DP.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = unit.name.lowercase(),
                    fontSize = SEGMENT_FONT_SIZE_SP.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isActive) Color.White else ScribbleFitColors.MidGray
                )
            }
        }
    }
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(DIVIDER_HEIGHT_DP.dp)
            .background(ScribbleFitColors.LightGray)
    )
}

@Composable
private fun ProviderPickerDialog(
    currentProvider: LLMProvider,
    onProviderSelected: (LLMProvider) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Choose Provider") },
        text = {
            Column {
                LLMProvider.entries.forEach { provider ->
                    val isSelected = provider == currentProvider
                    TextButton(
                        onClick = { onProviderSelected(provider) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = provider.rawValue.replaceFirstChar { it.uppercase() },
                            color = if (isSelected) ScribbleFitColors.RichBlack else ScribbleFitColors.MidGray,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Cancel") }
        }
    )
}

@Composable
private fun ModelPickerDialog(
    models: List<String>,
    onModelSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Choose Model") },
        text = {
            Column {
                models.forEach { model ->
                    TextButton(
                        onClick = { onModelSelected(model) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = model, color = ScribbleFitColors.RichBlack)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Cancel") }
        }
    )
}

@Composable
private fun ThemePickerDialog(
    currentTheme: ThemePreference,
    onThemeSelected: (ThemePreference) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Choose Theme") },
        text = {
            Column {
                ThemePreference.entries.forEach { theme ->
                    val isSelected = theme == currentTheme
                    TextButton(
                        onClick = { onThemeSelected(theme) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = theme.name.replaceFirstChar { it.uppercase() },
                            color = if (isSelected) ScribbleFitColors.RichBlack else ScribbleFitColors.MidGray,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Cancel") }
        }
    )
}
