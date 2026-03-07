package com.scribblefit.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.scribblefit.feature.profile.domain.model.*

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
            
            PreferenceItem("Parsing Mode") {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = uiState.settings.parsingMode == ParsingMode.CLOUD,
                        onClick = { viewModel.updateParsingMode(ParsingMode.CLOUD) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) { Text("Cloud") }
                    SegmentedButton(
                        selected = uiState.settings.parsingMode == ParsingMode.PERSONAL,
                        onClick = { viewModel.updateParsingMode(ParsingMode.PERSONAL) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) { Text("BYOK") }
                }
            }

            if (uiState.settings.parsingMode == ParsingMode.PERSONAL) {
                Spacer(modifier = Modifier.height(ScribbleFitSpacing.Medium))
                Text(text = "API Key", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                ScribbleFitTextField(
                    value = uiState.apiKey,
                    onValueChange = viewModel::updateApiKey,
                    placeholder = "sk-...",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(ScribbleFitSpacing.Medium))
                PreferenceItem("AI Provider") {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = uiState.settings.aiProvider == LLMProvider.OPENAI,
                            onClick = { viewModel.updateProvider(LLMProvider.OPENAI) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) { Text("OpenAI") }
                        SegmentedButton(
                            selected = uiState.settings.aiProvider == LLMProvider.GEMINI,
                            onClick = { viewModel.updateProvider(LLMProvider.GEMINI) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) { Text("Gemini") }
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
            SectionHeader("DANGER ZONE", color = Color(0xFFFF3B30))
            TextButton(
                onClick = viewModel::onClearDataClick,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFF3B30)),
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
