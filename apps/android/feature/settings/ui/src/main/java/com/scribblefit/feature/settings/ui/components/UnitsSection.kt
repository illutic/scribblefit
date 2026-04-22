package com.scribblefit.feature.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.SegmentedSelector
import com.scribblefit.feature.settings.ui.SettingsSection
import com.scribblefit.feature.settings.ui.SettingsState

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
