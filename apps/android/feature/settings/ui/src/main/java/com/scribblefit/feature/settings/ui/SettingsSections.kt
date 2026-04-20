package com.scribblefit.feature.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.SegmentedSelector

@Composable
internal fun AppearanceSection(
    state: SettingsState,
    onThemeChange: (ThemePreference) -> Unit,
    onDynamicThemeToggle: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    SettingsSection(title = state.appearanceTitle) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.themeTitle,
                style = ScribbleFitTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = state.themeLabel,
                        style = ScribbleFitTheme.typography.bodyMedium,
                        color = ScribbleFitTheme.colors.midGray
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = ScribbleFitTheme.colors.midGray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(ScribbleFitTheme.colors.surfaceContainerLow)
                ) {
                    DropdownMenuItem(
                        text = { Text(state.themeLightLabel) },
                        onClick = {
                            onThemeChange(ThemePreference.LIGHT)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(state.themeDarkLabel) },
                        onClick = {
                            onThemeChange(ThemePreference.DARK)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(state.themeSystemLabel) },
                        onClick = {
                            onThemeChange(ThemePreference.SYSTEM)
                            expanded = false
                        }
                    )
                }
            }
        }

        Divider(color = ScribbleFitTheme.colors.surfaceContainer, thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_dynamic_theme),
                    style = ScribbleFitTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.settings_dynamic_theme_description),
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray
                )
            }
            Switch(
                checked = state.isDynamicTheme,
                onCheckedChange = onDynamicThemeToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ScribbleFitTheme.colors.onPrimary,
                    checkedTrackColor = ScribbleFitTheme.colors.primary,
                    uncheckedThumbColor = ScribbleFitTheme.colors.midGray,
                    uncheckedTrackColor = ScribbleFitTheme.colors.surfaceContainer
                )
            )
        }
    }
}

@Composable
internal fun UnitsSection(
    state: SettingsState,
    onUnitChange: (Weight) -> Unit
) {
    SettingsSection(title = state.unitsTitle) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.weightPreferenceLabel,
                style = ScribbleFitTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            SegmentedSelector(
                options = listOf(
                    Weight.KGS to "kg",
                    Weight.LBS to "lbs"
                ),
                selectedOption = state.weightUnit,
                onOptionSelected = onUnitChange,
                modifier = Modifier.width(120.dp)
            )
        }
    }
}

@Composable
internal fun AIConfigurationSection(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit
) {
    SettingsSection(title = state.aiEngineTitle) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.providerLabel,
                    style = ScribbleFitTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (state.llmProvider == LLMProvider.GEMINI) 
                        "Cloud-based parsing via Gemini for Firebase" 
                    else 
                        "On-device parsing via Gemini Nano",
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray
                )
            }
            SegmentedSelector(
                options = listOf(
                    LLMProvider.GEMINI to "Cloud",
                    LLMProvider.LOCAL to "Local"
                ),
                selectedOption = state.llmProvider,
                onOptionSelected = { onIntent(SettingsIntent.ChangeAIProvider(it)) },
                modifier = Modifier.width(160.dp)
            )
        }

        AnimatedVisibility(
            visible = state.llmProvider == LLMProvider.LOCAL && !state.isLocalSupported,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Divider(
                    color = ScribbleFitTheme.colors.surfaceContainer,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = ScribbleFitTheme.spacing.small)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Error,
                        contentDescription = null,
                        tint = ScribbleFitTheme.colors.dangerRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = state.aiLocalUnsupportedError,
                        style = ScribbleFitTheme.typography.bodyMedium,
                        color = ScribbleFitTheme.colors.dangerRed
                    )
                }
            }
        }
    }
}

@Composable
internal fun DataManagementSection(
    state: SettingsState,
    onExport: () -> Unit,
    onClearData: () -> Unit
) {
    SettingsSection(title = state.dataTitle) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExport() }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = state.exportWorkoutLedgerLabel,
                    style = ScribbleFitTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = ScribbleFitTheme.colors.primary
                )
                Text(
                    text = state.jsonFormatLabel,
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray
                )
            }
            Icon(
                imageVector = Icons.Rounded.Download,
                contentDescription = null,
                tint = ScribbleFitTheme.colors.midGray,
                modifier = Modifier.size(20.dp)
            )
        }

        Divider(color = ScribbleFitTheme.colors.surfaceContainer, thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClearData() }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.clearAllDataLabel,
                style = ScribbleFitTheme.typography.bodyMedium,
                color = ScribbleFitTheme.colors.dangerRed,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Rounded.DeleteForever,
                contentDescription = null,
                tint = ScribbleFitTheme.colors.dangerRed,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
    ) {
        Text(
            text = title.uppercase(),
            style = ScribbleFitTheme.typography.labelMedium,
            color = ScribbleFitTheme.colors.midGray,
            modifier = Modifier.padding(horizontal = ScribbleFitTheme.spacing.small)
        )
        Surface(
            color = ScribbleFitTheme.colors.surfaceContainerLow,
            shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium),
                content = content
            )
        }
    }
}


@Composable
private fun Divider(
    color: Color,
    thickness: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}
