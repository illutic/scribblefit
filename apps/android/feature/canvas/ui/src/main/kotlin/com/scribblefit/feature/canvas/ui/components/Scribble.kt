package com.scribblefit.feature.canvas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Set

// TODO - replace hardcoded strings with actual strings from resources

@Composable
fun Scribble(
    scribble: Scribble,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (scribble.status) {
        ScribbleStatus.RAW -> PendingScribble(scribble, modifier)
        ScribbleStatus.IN_PROGRESS -> InProgressScribble(scribble, modifier)
        ScribbleStatus.PARSED -> ParsedScribble(scribble, onClick, modifier)
        ScribbleStatus.COMPLETED -> CompletedScribble(scribble, onClick, modifier)
        ScribbleStatus.FAILED -> FailedScribble(scribble, modifier)
    }
}

@Composable
private fun PendingScribble(
    scribble: Scribble,
    modifier: Modifier = Modifier
) {
    ScribbleCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = scribble.rawText,
                style = ScribbleFitTheme.typography.bodyLarge,
                color = ScribbleFitTheme.colors.richBlack,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Pending",
                style = ScribbleFitTheme.typography.labelSmall,
                color = ScribbleFitTheme.colors.strongGray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun InProgressScribble(
    scribble: Scribble,
    modifier: Modifier = Modifier
) {
    ScribbleCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = scribble.rawText,
                style = ScribbleFitTheme.typography.bodyLarge,
                color = ScribbleFitTheme.colors.richBlack
            )

            CircularProgressIndicator(
                modifier = Modifier.size(ScribbleFitTheme.shapes.large),
                color = ScribbleFitTheme.colors.richBlack,
            )
        }
    }
}

@Composable
private fun ParsedScribble(
    scribble: Scribble,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScribbleCard(
        modifier = modifier.clickable { onClick() },
        border = BorderStroke(1.dp, ScribbleFitTheme.colors.lightGray)
    ) {
        ExerciseItems(exercises = scribble.exercises)
    }
}

@Composable
private fun CompletedScribble(
    scribble: Scribble,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScribbleCard(
        modifier = modifier.clickable { onClick() },
        border = BorderStroke(1.dp, ScribbleFitTheme.colors.successGreen.copy(alpha = 0.3f))
    ) {
        Box {
            Badge(
                text = "Completed",
                backgroundColor = ScribbleFitTheme.colors.successGreen.copy(alpha = 0.2f),
                contentColor = ScribbleFitTheme.colors.successGreen,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            ExerciseItems(exercises = scribble.exercises)
        }
    }
}

@Composable
private fun FailedScribble(
    scribble: Scribble,
    modifier: Modifier = Modifier
) {
    ScribbleCard(
        modifier = modifier,
        border = BorderStroke(1.dp, ScribbleFitTheme.colors.dangerRed.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Failed",
                tint = ScribbleFitTheme.colors.dangerRed,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(ScribbleFitTheme.spacing.medium))
            Column {
                Text(
                    text = scribble.rawText,
                    style = ScribbleFitTheme.typography.bodyLarge,
                    color = ScribbleFitTheme.colors.richBlack
                )
                Text(
                    text = "Failed to parse. Tap to retry or edit.",
                    style = ScribbleFitTheme.typography.labelSmall,
                    color = ScribbleFitTheme.colors.dangerRed
                )
            }
        }
    }
}

// -- -- Common Card Layout for Scribble States -- --

@Composable
private fun ScribbleCard(
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = ScribbleFitTheme.colors.background
        ),
        border = border ?: BorderStroke(1.dp, ScribbleFitTheme.colors.softGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.padding(ScribbleFitTheme.spacing.medium)) {
            content()
        }
    }
}

@Composable
private fun ExerciseItems(
    exercises: List<Exercise>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium),
    ) {
        exercises.forEach { exercise ->
            ExerciseItem(exercise = exercise)
        }
    }
}

@Composable
private fun ExerciseItem(
    exercise: Exercise,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = exercise.canonicalName,
            style = ScribbleFitTheme.typography.titleMedium,
            color = ScribbleFitTheme.colors.richBlack,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow {
            exercise.sets.forEach { set ->
                Text(
                    text = "${set.setNumber} • ${set.weight}kg x ${set.reps}${if (set.rpe != null) " @ RPE ${set.rpe}" else ""}",
                    style = ScribbleFitTheme.typography.bodyMedium,
                    color = ScribbleFitTheme.colors.strongGray,
                    modifier = Modifier.padding(end = ScribbleFitTheme.spacing.medium)
                )
            }
        }
    }
}

@Composable
private fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ScribbleFitTheme.colors.softGray,
    contentColor: Color = ScribbleFitTheme.colors.richBlack
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = ScribbleFitTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

@Composable
@PreviewLightDark
private fun ScribblePreview() {
    val scribbles = listOf(
        Scribble(
            id = 1,
            rawText = "Bench Press 3x10 @ 70kg",
            status = ScribbleStatus.RAW,
            exercises = emptyList(),
            createdAt = System.currentTimeMillis()
        ),
        Scribble(
            id = 2,
            rawText = "Squat 4x8 @ 80kg",
            status = ScribbleStatus.IN_PROGRESS,
            exercises = emptyList(),
            createdAt = System.currentTimeMillis()
        ),
        Scribble(
            id = 3,
            rawText = "Deadlift 5x5 @ 100kg",
            status = ScribbleStatus.PARSED,
            createdAt = System.currentTimeMillis(),
            exercises = listOf(
                Exercise(
                    id = 1,
                    canonicalName = "Deadlift",
                    muscleGroup = "Back",
                    sets = listOf(
                        Set(
                            id = 1,
                            setNumber = 1,
                            weight = 100f,
                            reps = 5,
                            rpe = null,
                            notes = null
                        ),
                        Set(
                            id = 2,
                            setNumber = 2,
                            weight = 100f,
                            reps = 5,
                            rpe = null,
                            notes = null
                        ),
                        Set(
                            id = 3,
                            setNumber = 3,
                            weight = 100f,
                            reps = 5,
                            rpe = null,
                            notes = null
                        ),
                        Set(
                            id = 4,
                            setNumber = 4,
                            weight = 100f,
                            reps = 5,
                            rpe = null,
                            notes = null
                        ),
                        Set(
                            id = 5,
                            setNumber = 5,
                            weight = 100f,
                            reps = 5,
                            rpe = null,
                            notes = null
                        )
                    ),
                    isDraft = false,
                )
            )
        ),
        Scribble(
            id = 4,
            rawText = "Overhead Press 3x12 @ 40kg",
            status = ScribbleStatus.COMPLETED,
            createdAt = System.currentTimeMillis(),
            exercises = listOf(
                Exercise(
                    id = 2,
                    canonicalName = "Overhead Press",
                    muscleGroup = "Shoulders",
                    sets = listOf(
                        Set(
                            id = 6,
                            setNumber = 1,
                            weight = 40f,
                            reps = 12,
                            rpe = null,
                            notes = null
                        ),
                        Set(
                            id = 7,
                            setNumber = 2,
                            weight = 40f,
                            reps = 12,
                            rpe = null,
                            notes = null
                        ),
                        Set(
                            id = 8,
                            setNumber = 3,
                            weight = 40f,
                            reps = 12,
                            rpe = null,
                            notes = null
                        )
                    ),
                    isDraft = false,
                )
            )
        ),
        Scribble(
            id = 5,
            rawText = "Pull-ups 4xFailure",
            status = ScribbleStatus.FAILED,
            exercises = emptyList(),
            createdAt = System.currentTimeMillis()
        )
    )
    ScribbleFitTheme {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(scribbles) { scribble ->
                Scribble(scribble = scribble, onClick = { })
            }
        }
    }
}
