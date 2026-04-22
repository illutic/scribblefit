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
    onClick: () -> Unit,
    onExerciseClick: ((exerciseName: String) -> Unit),
    onIntent: (CanvasIntent) -> Unit
) {
    when (scribble.status) {
        ScribbleStatus.PENDING, ScribbleStatus.PARSING -> PendingScribbleCard(state, scribble)
        ScribbleStatus.SUCCESS -> ParsedScribbleCard(state, scribble, onClick)
        ScribbleStatus.COMPLETED -> LoggedScribbleCard(state, scribble, onClick, onExerciseClick)
        ScribbleStatus.FAILED -> FailedScribbleCard(
            state = state,
            scribble = scribble,
            onIntent = onIntent
        )
    }
}
