package com.scribblefit.feature.canvas.ui.components.card

import androidx.compose.runtime.Composable
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.canvas.ui.CanvasIntent
import com.scribblefit.feature.canvas.ui.CanvasState
import com.scribblefit.feature.canvas.ui.ScribbleUiModel

@Composable
internal fun ScribbleCard(
    state: CanvasState,
    scribble: ScribbleUiModel,
    onIntent: (CanvasIntent) -> Unit
) {
    when (scribble.status) {
        ScribbleStatus.PENDING, ScribbleStatus.PARSING -> PendingScribbleCard(
            state = state,
            scribble = scribble,
        )

        ScribbleStatus.SUCCESS -> ParsedScribbleCard(
            state = state,
            scribble = scribble,
            onClick = { onIntent(CanvasIntent.ClickOnScribble(scribble.scribble)) },
        )

        ScribbleStatus.COMPLETED -> LoggedScribbleCard(
            state = state,
            scribble = scribble,
            onExerciseClick = { exerciseId ->
                onIntent(CanvasIntent.NavigateToExerciseDetails(exerciseId))
            }
        )

        ScribbleStatus.FAILED -> FailedScribbleCard(
            state = state,
            scribble = scribble,
            onIntent = onIntent
        )
    }
}
