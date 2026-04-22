package com.scribblefit.feature.exercises.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.scribbleGlass
import com.scribblefit.core.model.ExercisePerformanceInsight

@Composable
fun ExerciseInsightCard(
    insight: ExercisePerformanceInsight?,
    isGenerating: Boolean,
    recommendationLabel: String,
    noInsightsText: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scribbleGlass(cornerRadius = ScribbleFitTheme.shapes.large),
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.large),
        color = ScribbleFitTheme.colors.surfaceContainerLow.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ScribbleFitTheme.spacing.large),
            contentAlignment = Alignment.Center
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    color = ScribbleFitTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else if (insight != null) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥",
                            style = ScribbleFitTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = ScribbleFitTheme.spacing.small)
                        )
                        Text(
                            text = recommendationLabel,
                            style = ScribbleFitTheme.typography.labelSmall,
                            color = ScribbleFitTheme.colors.midGray,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    
                    Text(
                        text = insight.breakdownText,
                        style = ScribbleFitTheme.typography.bodyMedium,
                        color = ScribbleFitTheme.colors.primary,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = ScribbleFitTheme.spacing.smallLarger)
                    )
                }
            } else {
                Text(
                    text = noInsightsText,
                    style = ScribbleFitTheme.typography.bodyMedium,
                    color = ScribbleFitTheme.colors.midGray
                )
            }
        }
    }
}
