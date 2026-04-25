package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.ledger.ui.LedgerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScribbleDetailsBottomSheet(
    state: LedgerState,
    onExerciseClick: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val scribble = state.selectedScribble ?: return
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ScribbleFitTheme.colors.surface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = ScribbleFitTheme.colors.midGray.copy(alpha = 0.2f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScribbleFitTheme.spacing.large)
                .padding(bottom = ScribbleFitTheme.spacing.large)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large)
            ) {
                ScribbleHeader(
                    title = state.scribbleDetailsTitle,
                    statusLabel = state.loggedLabel
                )

                RawTextSection(scribble = scribble)

                ExercisesSection(
                    scribble = scribble,
                    state = state,
                    exercisesLabel = state.exercisesLabel,
                    onExerciseClick = onExerciseClick
                )

                Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.medium))
            }
        }
    }
}

@Composable
private fun ScribbleHeader(
    title: String,
    statusLabel: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = ScribbleFitTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = ScribbleFitTheme.colors.primary
        )

        Surface(
            shape = RoundedCornerShape(4.dp),
            color = ScribbleFitTheme.colors.successGreen.copy(alpha = 0.1f)
        ) {
            Text(
                text = statusLabel,
                style = ScribbleFitTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = ScribbleFitTheme.colors.successGreen,
                modifier = Modifier.padding(
                    horizontal = ScribbleFitTheme.spacing.small,
                    vertical = ScribbleFitTheme.spacing.extraSmall
                )
            )
        }
    }
}

@Composable
private fun RawTextSection(scribble: Scribble) {
    Surface(
        shape = RoundedCornerShape(ScribbleFitTheme.spacing.medium),
        color = ScribbleFitTheme.colors.surfaceContainerLow.copy(alpha = 0.5f)
    ) {
        Text(
            text = scribble.rawText,
            style = ScribbleFitTheme.typography.bodyLarge,
            color = ScribbleFitTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(ScribbleFitTheme.spacing.medium)
        )
    }
}

@Composable
private fun ExercisesSection(
    scribble: Scribble,
    state: LedgerState,
    exercisesLabel: String,
    onExerciseClick: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)) {
        Text(
            text = exercisesLabel,
            style = ScribbleFitTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = ScribbleFitTheme.colors.midGray,
            letterSpacing = 1.sp
        )

        scribble.exercises.forEach { exercise ->
            ExerciseRow(
                exercise = exercise,
                summary = state.formatExerciseSummary(exercise),
                onClick = { onExerciseClick(exercise.id) }
            )
        }
    }
}

@Composable
private fun ExerciseRow(
    exercise: Exercise,
    summary: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(ScribbleFitTheme.spacing.medium),
        color = ScribbleFitTheme.colors.surfaceContainerLow.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ScribbleFitTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.extraSmall)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
            ) {
                Text(
                    text = exercise.canonicalName,
                    style = ScribbleFitTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.primary
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ScribbleFitTheme.colors.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = exercise.muscleGroup,
                        style = ScribbleFitTheme.typography.labelSmall,
                        color = ScribbleFitTheme.colors.primary,
                        modifier = Modifier.padding(
                            horizontal = ScribbleFitTheme.spacing.small,
                            vertical = 2.dp
                        )
                    )
                }
            }

            if (summary.isNotEmpty()) {
                Text(
                    text = summary,
                    style = ScribbleFitTheme.typography.bodyMedium,
                    color = ScribbleFitTheme.colors.midGray
                )
            }
        }
    }
}
