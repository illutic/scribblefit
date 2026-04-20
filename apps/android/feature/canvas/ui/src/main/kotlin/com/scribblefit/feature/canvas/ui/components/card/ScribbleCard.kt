package com.scribblefit.feature.canvas.ui.components.card

import androidx.compose.runtime.Composable
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.canvas.ui.CanvasIntent
import com.scribblefit.feature.canvas.ui.ScribbleUiModel

@Composable
internal fun ScribbleCard(
    scribble: ScribbleUiModel,
    onClick: () -> Unit,
    onIntent: (CanvasIntent) -> Unit
) {
    when (scribble.status) {
        ScribbleStatus.PENDING, ScribbleStatus.PARSING -> PendingScribbleCard(scribble)
        ScribbleStatus.SUCCESS -> ParsedScribbleCard(scribble, onClick)
        ScribbleStatus.COMPLETED -> LoggedScribbleCard(scribble, onClick)
        ScribbleStatus.FAILED -> FailedScribbleCard(
            scribble = scribble,
            onIntent = onIntent
        )
    }
}
