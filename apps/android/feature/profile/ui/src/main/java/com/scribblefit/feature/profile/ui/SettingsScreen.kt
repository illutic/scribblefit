package com.scribblefit.feature.profile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.core.designsystem.component.ScribbleFitTextField
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitSpacing
import com.scribblefit.feature.profile.domain.model.AppSettings
import com.scribblefit.feature.profile.domain.model.ParsingMode
import com.scribblefit.feature.profile.domain.model.ThemePreference
import com.scribblefit.feature.profile.domain.model.WeightUnit

private val DangerRed = Color(0xFFFF3B30)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = viewModel::onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScribbleFitSpacing.ScreenPadding)
        ) {
            // AI Engine Section
            SectionHeader("AI ENGINE")

            PreferenceItem("Provider") {
                ProviderDropdown(
                    selected = uiState.settings.aiProvider,
                    onSelect = viewModel::updateProvider
                )
            }

            val provider = uiState.settings.aiProvider
            if (provider == LLMProvider.OPENAI || provider == LLMProvider.GEMINI) {
                Spacer(modifier = Modifier.height(ScribbleFitSpacing.Medium))
                Text(
                    text = "API Key",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                ScribbleFitTextField(
                    value = uiState.apiKey,
                    onValueChange = viewModel::updateApiKey,
                    placeholder = if (provider == LLMProvider.OPENAI) "sk-..." else "AIza...",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(ScribbleFitSpacing.Medium))
                PreferenceItem("Model") {
                    when {
                        uiState.isLoadingModels -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Loading models...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        uiState.availableModels.isEmpty() -> {
                            TextButton(
                                onClick = viewModel::fetchModels,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = when {
                                        uiState.apiKey.isEmpty() -> "Enter API key to load models"
                                        uiState.modelLoadError != null -> uiState.modelLoadError!!
                                        else -> "Tap to load models"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (uiState.modelLoadError != null) DangerRed else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        else -> {
                            ModelDropdown(
                                selected = uiState.settings.selectedModel.ifEmpty { uiState.availableModels.firstOrNull() ?: "" },
                                models = uiState.availableModels,
                                onSelect = viewModel::updateModel
                            )
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 24.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            // Preferences Section
            SectionHeader("PREFERENCES")

            PreferenceItem("Weight Units") {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = uiState.settings.weightUnit == WeightUnit.LBS,
                        onClick = { viewModel.updateWeightUnit(WeightUnit.LBS) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) { Text("lbs") }
                    SegmentedButton(
                        selected = uiState.settings.weightUnit == WeightUnit.KG,
                        onClick = { viewModel.updateWeightUnit(WeightUnit.KG) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) { Text("kg") }
                }
            }

            Spacer(modifier = Modifier.height(ScribbleFitSpacing.Large))

            // Danger Zone
            SectionHeader("DANGER ZONE", color = DangerRed)
            TextButton(
                onClick = viewModel::onClearDataClick,
                colors = ButtonDefaults.textButtonColors(contentColor = DangerRed),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Clear All Data",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(ScribbleFitSpacing.Huge))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProviderDropdown(
    selected: LLMProvider,
    onSelect: (LLMProvider) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        LLMProvider.PROXY to "ScribbleFit AI",
        LLMProvider.OPENAI to "OpenAI",
        LLMProvider.GEMINI to "Gemini",
        LLMProvider.LOCAL to "Local"
    )

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = options.first { it.first == selected }.second,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (provider, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { onSelect(provider); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelDropdown(
    selected: String,
    models: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = { Text(model) },
                    onClick = { onSelect(model); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        ),
        color = color,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
private fun PreferenceItem(label: String, content: @Composable () -> Unit) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}
