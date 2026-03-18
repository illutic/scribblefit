package com.scribblefit.feature.canvas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Set
import com.scribblefit.feature.canvas.ui.R

@Composable
internal fun Scribble(
    scribble: Scribble,
    onClick: (Scribble) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (scribble.status) {
        ScribbleStatus.PENDING -> PendingScribble(scribble, modifier)
        ScribbleStatus.PARSING -> ParsingScribble(scribble, modifier)
        ScribbleStatus.SUCCESS -> SuccessScribble(scribble, { onClick(scribble) }, modifier)
        ScribbleStatus.COMPLETED -> CompletedScribble(scribble, modifier)
        ScribbleStatus.FAILED -> FailedScribble(scribble, { onClick(scribble) }, modifier)
    }
}

@Composable
private fun PendingScribble(
    scribble: Scribble,
    modifier: Modifier = Modifier,
) {
    ScribbleCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = scribble.rawText,
                style = ScribbleFitTheme.typography.bodyLarge,
                color = ScribbleFitTheme.colors.richBlack,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.canvas_status_pending),
                style = ScribbleFitTheme.typography.labelSmall,
                color = ScribbleFitTheme.colors.strongGray,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ParsingScribble(
    scribble: Scribble,
    modifier: Modifier = Modifier,
) {
    ScribbleCard(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = scribble.rawText,
                style = ScribbleFitTheme.typography.bodyLarge,
                color = ScribbleFitTheme.colors.richBlack,
            )

            CircularProgressIndicator(
                modifier = Modifier.size(ScribbleFitTheme.spacing.large),
                color = ScribbleFitTheme.colors.richBlack,
            )
        }
    }
}

@Composable
private fun SuccessScribble(
    scribble: Scribble,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScribbleCard(
        modifier = modifier.clickable { onClick() },
        border = BorderStroke(1.dp, ScribbleFitTheme.colors.lightGray),
    ) {
        ExerciseItems(exercises = scribble.exercises)
    }
}

@Composable
private fun CompletedScribble(
    scribble: Scribble,
    modifier: Modifier = Modifier,
) {
    ScribbleCard(
        modifier = modifier,
        border = BorderStroke(1.dp, ScribbleFitTheme.colors.successGreen.copy(alpha = 0.3f)),
    ) {
        Box {
            Badge(
                text = stringResource(R.string.canvas_status_completed),
                backgroundColor = ScribbleFitTheme.colors.successGreen.copy(alpha = 0.2f),
                contentColor = ScribbleFitTheme.colors.successGreen,
                modifier = Modifier.align(Alignment.TopEnd),
            )

            ExerciseItems(exercises = scribble.exercises)
        }
    }
}

@Composable
private fun FailedScribble(
    scribble: Scribble,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScribbleCard(
        modifier = modifier.clickable { onClick() },
        border = BorderStroke(1.dp, ScribbleFitTheme.colors.dangerRed.copy(alpha = 0.3f)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = scribble.rawText,
                style = ScribbleFitTheme.typography.bodyLarge,
                color = ScribbleFitTheme.colors.richBlack,
            )

            Badge(
                text = stringResource(R.string.canvas_status_failed),
                backgroundColor = ScribbleFitTheme.colors.dangerRed.copy(alpha = 0.2f),
                contentColor = ScribbleFitTheme.colors.dangerRed,
            )
        }
    }
}

@Composable
private fun ScribbleCard(
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ScribbleFitTheme.colors.background,
        border = border,
        shadowElevation = 2.dp,
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun ExerciseItems(exercises: List<Exercise>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        exercises.forEach { exercise ->
            Column {
                Text(
                    text = exercise.canonicalName,
                    style = ScribbleFitTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.richBlack,
                )
                exercise.sets.forEach { set ->
                    Text(
                        text = "${set.setNumber}. ${set.weight}kg x ${set.reps}",
                        style = ScribbleFitTheme.typography.bodyMedium,
                        color = ScribbleFitTheme.colors.strongGray,
                    )
                }
            }
        }
    }
}

@Composable
private fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ScribbleFitTheme.colors.softGray,
    contentColor: Color = ScribbleFitTheme.colors.richBlack,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(4.dp))
                .background(backgroundColor)
                .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            style = ScribbleFitTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}

@Composable
@PreviewLightDark
private fun ScribblePreview() {
    val scribbles =
        listOf(
            Scribble(
                id = 1,
                rawText = "Bench Press 3x10 @ 70kg",
                status = ScribbleStatus.PENDING,
                exercises = emptyList(),
                createdAt = System.currentTimeMillis(),
            ),
            Scribble(
                id = 2,
                rawText = "Squat 4x8 @ 80kg",
                status = ScribbleStatus.PARSING,
                exercises = emptyList(),
                createdAt = System.currentTimeMillis(),
            ),
            Scribble(
                id = 3,
                rawText = "Deadlift 5x5 @ 100kg",
                status = ScribbleStatus.SUCCESS,
                createdAt = System.currentTimeMillis(),
                exercises =
                    listOf(
                        Exercise(
                            id = 1,
                            canonicalName = "Deadlift",
                            muscleGroup = "Back",
                            sets =
                                listOf(
                                    Set(
                                        id = 1,
                                        setNumber = 1,
                                        weight = 100f,
                                        reps = 5,
                                        rpe = null,
                                        notes = null,
                                    ),
                                ),
                            isDraft = false,
                        ),
                    ),
            ),
        )
    ScribbleFitTheme {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(scribbles) { scribble ->
                Scribble(scribble = scribble, onClick = { })
            }
        }
    }
}
