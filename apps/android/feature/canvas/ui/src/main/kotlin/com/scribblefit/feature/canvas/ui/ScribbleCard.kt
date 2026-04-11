package com.scribblefit.feature.canvas.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus

@Composable
internal fun ScribbleCard(
    scribble: Scribble,
    weightUnit: Weight,
    onClick: () -> Unit,
    onIntent: (CanvasIntent) -> Unit,
) {
    when (scribble.status) {
        ScribbleStatus.PENDING, ScribbleStatus.PARSING -> PendingScribbleCard(scribble)
        ScribbleStatus.SUCCESS -> ParsedScribbleCard(scribble, weightUnit, onClick)
        ScribbleStatus.COMPLETED -> LoggedScribbleCard(scribble, weightUnit, onClick)
        ScribbleStatus.FAILED -> FailedScribbleCard(scribble, onIntent)
    }
}

@Composable
private fun PendingScribbleCard(scribble: Scribble) {
    val infiniteTransition = rememberInfiniteTransition(label = "parsing")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "\"${scribble.rawText}\"",
                    style = ScribbleFitTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    color = ScribbleFitTheme.colors.midGray,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Rounded.Sync,
                    contentDescription = null,
                    tint = ScribbleFitTheme.colors.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(ScribbleFitTheme.colors.surfaceContainerHigh, CircleShape)
            ) {
                val progressWidth by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "progress"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressWidth)
                        .fillMaxHeight()
                        .background(ScribbleFitTheme.colors.primary, CircleShape)
                )
            }

            Text(
                text = stringResource(R.string.canvas_parsing_workout_data).uppercase(),
                style = ScribbleFitTheme.typography.labelMedium,
                color = ScribbleFitTheme.colors.primary.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun ParsedScribbleCard(
    scribble: Scribble,
    weightUnit: Weight,
    onClick: () -> Unit
) {
    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ScribbleFitTheme.colors.primary.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "\"${scribble.rawText}\"",
                        style = ScribbleFitTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = ScribbleFitTheme.colors.midGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    scribble.exercises.firstOrNull()?.let { exercise ->
                        Text(
                            text = exercise.canonicalName,
                            style = ScribbleFitTheme.typography.displayLarge.copy(fontSize = 32.sp),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        )
                        val totalSets = exercise.sets.size
                        val repsPerSet = exercise.sets.firstOrNull()?.reps ?: 0
                        val weightValue = exercise.sets.firstOrNull()?.weight ?: 0f
                        val unit = stringResource(
                            if (weightUnit == Weight.KGS) R.string.canvas_weight_unit_kg
                            else R.string.canvas_weight_unit_lb
                        )

                        Text(
                            text = stringResource(
                                R.string.canvas_workout_summary_format,
                                weightValue,
                                unit,
                                totalSets,
                                repsPerSet
                            ),
                            style = ScribbleFitTheme.typography.bodyMedium,
                            color = ScribbleFitTheme.colors.midGray
                        )
                    }
                }
                Surface(
                    color = ScribbleFitTheme.colors.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.DoneAll,
                            contentDescription = null,
                            tint = ScribbleFitTheme.colors.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Surface(
                color = ScribbleFitTheme.colors.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.wrapContentSize()
            ) {
                Text(
                    text = stringResource(R.string.canvas_tap_to_confirm).uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun LoggedScribbleCard(
    scribble: Scribble,
    weightUnit: Weight,
    onClick: () -> Unit
) {
    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .alpha(0.8f)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                scribble.exercises.firstOrNull()?.let { exercise ->
                    Column {
                        Text(
                            text = exercise.canonicalName,
                            style = ScribbleFitTheme.typography.displayLarge.copy(fontSize = 32.sp),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        )
                        val totalSets = exercise.sets.size
                        val repsPerSet = exercise.sets.firstOrNull()?.reps ?: 0
                        val weightValue = exercise.sets.firstOrNull()?.weight ?: 0f
                        val unit = stringResource(
                            if (weightUnit == Weight.KGS) R.string.canvas_weight_unit_kg
                            else R.string.canvas_weight_unit_lb
                        )

                        Text(
                            text = stringResource(
                                R.string.canvas_workout_summary_format,
                                weightValue,
                                unit,
                                totalSets,
                                repsPerSet
                            ),
                            style = ScribbleFitTheme.typography.bodyMedium,
                            color = ScribbleFitTheme.colors.midGray
                        )
                    }
                }
                Surface(
                    color = ScribbleFitTheme.colors.primary.copy(alpha = 0.05f),
                    shape = CircleShape,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = ScribbleFitTheme.colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.canvas_logged).uppercase(),
                            style = ScribbleFitTheme.typography.labelMedium,
                            color = ScribbleFitTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            scribble.exercises.firstOrNull()?.let { exercise ->
                if (exercise.estimated1RM != null || exercise.intensity != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        exercise.estimated1RM?.let { oneRm ->
                            StatCard(
                                label = stringResource(R.string.canvas_estimated_1rm),
                                value = stringResource(
                                    R.string.canvas_estimated_1rm_value_format,
                                    oneRm.toInt(),
                                    if (weightUnit == Weight.KGS) stringResource(R.string.canvas_weight_unit_kg) else stringResource(
                                        R.string.canvas_weight_unit_lb
                                    )
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        exercise.intensity?.let { intensity ->
                            StatCard(
                                label = stringResource(R.string.canvas_intensity),
                                value = stringResource(
                                    R.string.canvas_intensity_value_format,
                                    (intensity * 100).toInt()
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                exercise.improvement?.let { improvement ->
                    val sign = if (improvement >= 0) "+" else ""
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = null,
                            tint = ScribbleFitTheme.colors.midGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = stringResource(
                                R.string.canvas_last_session_improvement_format,
                                "$sign${improvement.toInt()}",
                                if (weightUnit == Weight.KGS) stringResource(R.string.canvas_weight_unit_kg) else stringResource(
                                    R.string.canvas_weight_unit_lb
                                )
                            ).uppercase(),
                            style = ScribbleFitTheme.typography.labelMedium,
                            color = ScribbleFitTheme.colors.midGray,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FailedScribbleCard(
    scribble: Scribble,
    onIntent: (CanvasIntent) -> Unit
) {
    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ScribbleFitTheme.colors.dangerRed.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "\"${scribble.rawText}\"",
                        style = ScribbleFitTheme.typography.titleMedium,
                        fontStyle = FontStyle.Italic,
                        color = ScribbleFitTheme.colors.midGray
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Error,
                            contentDescription = null,
                            tint = ScribbleFitTheme.colors.dangerRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = stringResource(R.string.canvas_failed_to_parse_workout).uppercase(),
                            style = ScribbleFitTheme.typography.labelMedium,
                            color = ScribbleFitTheme.colors.dangerRed,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
                Surface(
                    color = ScribbleFitTheme.colors.dangerRed.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.PriorityHigh,
                            contentDescription = null,
                            tint = ScribbleFitTheme.colors.dangerRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.canvas_retry).uppercase(),
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.clickable {
                        onIntent(
                            CanvasIntent.RetryScribbleParsing(
                                scribble
                            )
                        )
                    }
                )
                Text(
                    text = stringResource(R.string.canvas_remove).uppercase(),
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.clickable { onIntent(CanvasIntent.DeleteScribble(scribble.id)) }
                )
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLow,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label.uppercase(),
                style = ScribbleFitTheme.typography.labelMedium,
                color = ScribbleFitTheme.colors.midGray,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = value,
                style = ScribbleFitTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
