package com.scribblefit.feature.exercises.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme

@Composable
fun HistorySection(
    historyCount: Int,
    titleLabel: String,
    viewAllSessionsText: String,
    totalSessionsText: String,
    onViewHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = titleLabel,
            style = ScribbleFitTheme.typography.labelSmall,
            color = ScribbleFitTheme.colors.midGray,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Surface(
            onClick = onViewHistoryClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ScribbleFitTheme.spacing.medium),
            shape = RoundedCornerShape(ScribbleFitTheme.shapes.large),
            color = ScribbleFitTheme.colors.surfaceContainerLowest
        ) {
            Row(
                modifier = Modifier.padding(ScribbleFitTheme.spacing.large),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = ScribbleFitTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = ScribbleFitTheme.spacing.medium)
                        .weight(1f)
                ) {
                    Text(
                        text = viewAllSessionsText,
                        style = ScribbleFitTheme.typography.bodyLarge,
                        color = ScribbleFitTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = totalSessionsText,
                        style = ScribbleFitTheme.typography.bodySmall,
                        color = ScribbleFitTheme.colors.midGray
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ScribbleFitTheme.colors.midGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
