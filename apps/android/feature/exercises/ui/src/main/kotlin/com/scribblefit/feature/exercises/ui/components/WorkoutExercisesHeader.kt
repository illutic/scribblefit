package com.scribblefit.feature.exercises.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.TopBar
import com.scribblefit.core.designsystem.components.StatCard

@Composable
internal fun WorkoutExercisesHeader(
    dateString: String,
    totalExercises: Int,
    totalSets: Int,
    totalVolume: String,
    exercisesLabel: String,
    setsLabel: String,
    volumeLabel: String,
    backContentDescription: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                bottom = ScribbleFitTheme.spacing.medium
            ),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(
                    text = dateString,
                    style = ScribbleFitTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.primary
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = backContentDescription,
                        tint = ScribbleFitTheme.colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
        )

        Row(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .padding(horizontal = ScribbleFitTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium),
        ) {
            StatCard(
                label = exercisesLabel,
                value = totalExercises.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = setsLabel,
                value = totalSets.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = volumeLabel,
                value = totalVolume,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
