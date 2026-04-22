package com.scribblefit.feature.settings.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.settings.ui.SettingsIntent
import com.scribblefit.feature.settings.ui.SettingsState
import com.scribblefit.core.designsystem.SegmentedSelector
import com.scribblefit.feature.settings.ui.SettingsDivider
import com.scribblefit.feature.settings.ui.SettingsSection

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
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
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
                SettingsDivider(modifier = Modifier.padding(vertical = ScribbleFitTheme.spacing.small))
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
