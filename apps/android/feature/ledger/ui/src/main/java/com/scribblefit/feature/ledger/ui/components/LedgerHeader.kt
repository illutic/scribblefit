package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.scribbleGlass

@Composable
internal fun LedgerHeader(
    title: String,
    dateRange: String,
    onDateRangeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = ScribbleFitTheme.typography.headlineSmall,
            color = ScribbleFitTheme.colors.primary
        )
        
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        
        Surface(
            onClick = onDateRangeClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .scribbleGlass(cornerRadius = 12.dp),
            color = ScribbleFitTheme.colors.surfaceContainerLow.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateRange,
                    style = ScribbleFitTheme.typography.bodyMedium,
                    color = ScribbleFitTheme.colors.primary
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
