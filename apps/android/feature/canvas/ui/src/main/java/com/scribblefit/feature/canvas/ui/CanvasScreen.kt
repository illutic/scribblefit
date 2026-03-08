package com.scribblefit.feature.canvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scribblefit.core.designsystem.ScribbleFitColors
import com.scribblefit.core.designsystem.ScribbleFitShapes
import com.scribblefit.core.designsystem.ScribbleFitSpacing
import com.scribblefit.feature.ai.domain.model.ParsedExercise
import com.scribblefit.feature.canvas.domain.model.FeedItem
import com.scribblefit.feature.canvas.domain.model.ScribbleStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val TOP_NAV_TITLE_SP = 28
private const val DATE_HEADER_SP = 12
private const val EXERCISE_NAME_SP = 17
private const val SET_SUMMARY_SP = 15
private const val LOGGED_LABEL_SP = 12
private const val SCRIBBLE_TEXT_SP = 15
private const val PROMPT_TEXT_SP = 15
private const val PLACEHOLDER_SP = 15

private const val SETTINGS_ICON_DP = 22
private const val SETTINGS_TAP_TARGET_DP = 36
private const val TOP_NAV_TOP_PADDING_DP = 16
private const val TOP_NAV_BOTTOM_PADDING_DP = 12
private const val TOP_NAV_HORIZONTAL_PADDING_DP = 16

private const val DATE_HEADER_TOP_PADDING_DP = 24
private const val DATE_HEADER_BOTTOM_PADDING_DP = 8
private const val DATE_HEADER_HORIZONTAL_PADDING_DP = 16

private const val CARD_CORNER_RADIUS_DP = 12
private const val CARD_BORDER_WIDTH_DP = 1
private const val CARD_HORIZONTAL_PADDING_DP = 16
private const val CARD_VERTICAL_PADDING_DP = 14

private const val SCRIBBLE_PILL_HEIGHT_DP = 52
private const val SCRIBBLE_PILL_HORIZONTAL_PADDING_DP = 18
private const val STATUS_DOT_DP = 8

private const val INPUT_BAR_HEIGHT_DP = 52
private const val INPUT_BAR_HORIZONTAL_MARGIN_DP = 16
private const val INPUT_BAR_START_PADDING_DP = 18
private const val INPUT_SEND_BUTTON_DP = 32

private const val PROMPT_VERTICAL_PADDING_DP = 16

private val CapsuleShape = RoundedCornerShape(percent = 50)
private val CardShape = RoundedCornerShape(CARD_CORNER_RADIUS_DP.dp)

@Composable
fun CanvasScreen(
    modifier: Modifier = Modifier,
    onSettingsTap: () -> Unit = {},
    viewModel: CanvasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val groupedFeed = remember(uiState.feedItems) {
        buildGroupedFeed(uiState.feedItems)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScribbleFitColors.Background)
    ) {
        TopNavBar(onSettingsTap = onSettingsTap)

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            if (groupedFeed.isEmpty()) {
                item(key = "empty_state") {
                    EmptyState()
                }
            } else {
                groupedFeed.forEach { group ->
                    item(key = "header_${group.startOfDayMillis}") {
                        DateHeader(label = group.label)
                    }
                    items(group.items, key = { it.feedId() }) { item ->
                        FeedItemRow(
                            item = item,
                            onConfirm = viewModel::onConfirmClick,
                            onRetry = viewModel::onRetryScribble
                        )
                    }
                }
            }
        }

        InputBar(
            text = uiState.scribbleText,
            onTextChange = viewModel::onTextChange,
            onSubmit = viewModel::submitScribble,
            isSyncing = uiState.isSyncing,
            modifier = Modifier.padding(
                horizontal = INPUT_BAR_HORIZONTAL_MARGIN_DP.dp,
                vertical = ScribbleFitSpacing.Small
            )
        )
    }
}

@Composable
private fun TopNavBar(onSettingsTap: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = TOP_NAV_HORIZONTAL_PADDING_DP.dp,
                end = TOP_NAV_HORIZONTAL_PADDING_DP.dp,
                top = TOP_NAV_TOP_PADDING_DP.dp,
                bottom = TOP_NAV_BOTTOM_PADDING_DP.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ScribbleFit",
            color = ScribbleFitColors.RichBlack,
            fontSize = TOP_NAV_TITLE_SP.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onSettingsTap,
            modifier = Modifier.size(SETTINGS_TAP_TARGET_DP.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = ScribbleFitColors.RichBlack,
                modifier = Modifier.size(SETTINGS_ICON_DP.dp)
            )
        }
    }
}

@Composable
private fun DateHeader(label: String) {
    Text(
        text = label,
        color = ScribbleFitColors.MidGray,
        fontSize = DATE_HEADER_SP.sp,
        fontWeight = FontWeight.Normal,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = DATE_HEADER_HORIZONTAL_PADDING_DP.dp,
                end = DATE_HEADER_HORIZONTAL_PADDING_DP.dp,
                top = DATE_HEADER_TOP_PADDING_DP.dp,
                bottom = DATE_HEADER_BOTTOM_PADDING_DP.dp
            )
    )
}

@Composable
private fun FeedItemRow(
    item: FeedItem,
    onConfirm: (FeedItem.Confirmation) -> Unit,
    onRetry: (String) -> Unit
) {
    when (item) {
        is FeedItem.Scribble -> ScribbleCard(item = item, onRetry = onRetry)
        is FeedItem.Confirmation -> ConfirmationCard(item = item, onConfirm = onConfirm)
        is FeedItem.Prompt -> PromptCard(item = item)
        is FeedItem.Insight -> InsightCard(item = item)
    }
}

@Composable
private fun ScribbleCard(item: FeedItem.Scribble, onRetry: (String) -> Unit) {
    val isFailed = item.status == ScribbleStatus.FAILED
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScribbleFitSpacing.Medium, vertical = ScribbleFitSpacing.Small / 2)
            .height(SCRIBBLE_PILL_HEIGHT_DP.dp)
            .clip(CapsuleShape)
            .background(ScribbleFitColors.SoftGray)
            .then(
                if (isFailed) {
                    Modifier.clickable { onRetry(item.id) }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = SCRIBBLE_PILL_HORIZONTAL_PADDING_DP.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.rawText,
            color = ScribbleFitColors.RichBlack,
            fontSize = SCRIBBLE_TEXT_SP.sp,
            modifier = Modifier.weight(1f)
        )
        when (item.status) {
            ScribbleStatus.PENDING,
            ScribbleStatus.PROCESSING -> {
                Spacer(modifier = Modifier.width(ScribbleFitSpacing.Small))
                Box(
                    modifier = Modifier
                        .size(STATUS_DOT_DP.dp)
                        .clip(CircleShape)
                        .background(ScribbleFitColors.MidGray)
                )
            }
            ScribbleStatus.FAILED -> {
                Spacer(modifier = Modifier.width(ScribbleFitSpacing.Small))
                Box(
                    modifier = Modifier
                        .size(STATUS_DOT_DP.dp)
                        .clip(CircleShape)
                        .background(ScribbleFitColors.DangerRed)
                )
            }
            ScribbleStatus.COMPLETED -> Unit
        }
    }
}

@Composable
private fun ConfirmationCard(
    item: FeedItem.Confirmation,
    onConfirm: (FeedItem.Confirmation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScribbleFitSpacing.Medium, vertical = ScribbleFitSpacing.Small / 2)
            .clip(CardShape)
            .background(ScribbleFitColors.Background)
            .border(
                width = CARD_BORDER_WIDTH_DP.dp,
                color = ScribbleFitColors.LightGray,
                shape = CardShape
            )
            .padding(
                horizontal = CARD_HORIZONTAL_PADDING_DP.dp,
                vertical = CARD_VERTICAL_PADDING_DP.dp
            )
    ) {
        item.workout.exercises.forEachIndexed { index, exercise ->
            ExerciseRow(
                exercise = exercise,
                isFirst = index == 0,
                onConfirm = if (index == 0) {
                    { onConfirm(item) }
                } else {
                    null
                }
            )
            if (index < item.workout.exercises.lastIndex) {
                Spacer(modifier = Modifier.height(ScribbleFitSpacing.Small))
            }
        }
    }
}

@Composable
private fun ExerciseRow(
    exercise: ParsedExercise,
    isFirst: Boolean,
    onConfirm: (() -> Unit)?
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exercise.canonicalName,
                color = ScribbleFitColors.RichBlack,
                fontSize = EXERCISE_NAME_SP.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            if (isFirst && onConfirm != null) {
                Text(
                    text = "\u2713 Logged",
                    color = ScribbleFitColors.MidGray,
                    fontSize = LOGGED_LABEL_SP.sp,
                    modifier = Modifier.clickable { onConfirm() }
                )
            }
        }
        if (exercise.sets.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = buildSetSummary(exercise),
                color = ScribbleFitColors.MidGray,
                fontSize = SET_SUMMARY_SP.sp
            )
        }
    }
}

@Composable
private fun PromptCard(item: FeedItem.Prompt) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScribbleFitSpacing.Medium, vertical = PROMPT_VERTICAL_PADDING_DP.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.emoji, fontSize = PROMPT_TEXT_SP.sp)
        Spacer(modifier = Modifier.width(ScribbleFitSpacing.Small))
        Text(
            text = item.text,
            color = ScribbleFitColors.MidGray,
            fontSize = PROMPT_TEXT_SP.sp,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
private fun InsightCard(item: FeedItem.Insight) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScribbleFitSpacing.Medium, vertical = PROMPT_VERTICAL_PADDING_DP.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.emoji, fontSize = PROMPT_TEXT_SP.sp)
        Spacer(modifier = Modifier.width(ScribbleFitSpacing.Small))
        Text(
            text = item.text,
            color = ScribbleFitColors.MidGray,
            fontSize = PROMPT_TEXT_SP.sp,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 120.dp, start = ScribbleFitSpacing.Medium, end = ScribbleFitSpacing.Medium),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "\uD83C\uDFCB\uFE0F", fontSize = PROMPT_TEXT_SP.sp)
            Spacer(modifier = Modifier.width(ScribbleFitSpacing.Small))
            Text(
                text = "Start scribbling. Type your first set below.",
                color = ScribbleFitColors.MidGray,
                fontSize = PROMPT_TEXT_SP.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun InputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isSyncing: Boolean,
    modifier: Modifier = Modifier
) {
    val isTextEmpty = text.isEmpty()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(INPUT_BAR_HEIGHT_DP.dp)
            .clip(CapsuleShape)
            .background(ScribbleFitColors.SoftGray)
            .padding(start = INPUT_BAR_START_PADDING_DP.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (text.isEmpty()) {
                Text(
                    text = "What did you lift today?",
                    color = ScribbleFitColors.MidGray,
                    fontSize = PLACEHOLDER_SP.sp
                )
            }
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                textStyle = TextStyle(
                    color = ScribbleFitColors.RichBlack,
                    fontSize = PLACEHOLDER_SP.sp
                ),
                cursorBrush = SolidColor(ScribbleFitColors.RichBlack),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSubmit() }),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(INPUT_SEND_BUTTON_DP.dp)
                .clip(CircleShape)
                .background(ScribbleFitColors.SoftGray)
                .clickable(enabled = !isTextEmpty && !isSyncing) { onSubmit() },
            contentAlignment = Alignment.Center
        ) {
            if (isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = ScribbleFitColors.MidGray
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (isTextEmpty) ScribbleFitColors.MidGray else ScribbleFitColors.RichBlack,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// --- Grouping helpers ---

private data class FeedGroup(
    val startOfDayMillis: Long,
    val label: String,
    val items: List<FeedItem>
)

private fun buildGroupedFeed(feedItems: List<FeedItem>): List<FeedGroup> {
    if (feedItems.isEmpty()) return emptyList()

    val todayStart = startOfDay(System.currentTimeMillis())
    val yesterdayStart = todayStart - MILLIS_IN_DAY

    val grouped = feedItems.groupBy { startOfDay(it.timestamp()) }

    return grouped.entries
        .sortedByDescending { it.key }
        .map { (startMillis, items) ->
            FeedGroup(
                startOfDayMillis = startMillis,
                label = formatDateLabel(startMillis, todayStart, yesterdayStart),
                items = items.sortedBy { it.timestamp() }
            )
        }
}

private fun startOfDay(millis: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun formatDateLabel(startMillis: Long, todayStart: Long, yesterdayStart: Long): String {
    val formatter = SimpleDateFormat("MMMM d", Locale.getDefault())
    val dateString = formatter.format(Date(startMillis))
    return when (startMillis) {
        todayStart -> "Today, $dateString"
        yesterdayStart -> "Yesterday, $dateString"
        else -> dateString
    }
}

private fun buildSetSummary(exercise: ParsedExercise): String {
    val firstSet = exercise.sets.first()
    val setCount = exercise.sets.size
    val weightStr = if (firstSet.weight % 1.0 == 0.0) {
        firstSet.weight.toInt().toString()
    } else {
        firstSet.weight.toString()
    }
    return "$setCount sets \u00B7 $weightStr lb \u00B7 ${firstSet.reps} reps"
}

private fun FeedItem.timestamp(): Long = when (this) {
    is FeedItem.Prompt -> timestamp
    is FeedItem.Scribble -> timestamp
    is FeedItem.Confirmation -> timestamp
    is FeedItem.Insight -> timestamp
}

private fun FeedItem.feedId(): String = when (this) {
    is FeedItem.Prompt -> id
    is FeedItem.Scribble -> id
    is FeedItem.Confirmation -> id
    is FeedItem.Insight -> id
}

private const val MILLIS_IN_DAY = 86_400_000L
