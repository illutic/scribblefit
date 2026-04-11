package com.scribblefit.feature.canvas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.TopBar

@Composable
internal fun CanvasTopBar(
    dateString: String,
    onPreviousDayClick: () -> Unit,
    onNextDayClick: () -> Unit,
    onDateClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Surface(
        color = ScribbleFitTheme.colors.surface.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxWidth()
    ) {
        TopBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onPreviousDayClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = stringResource(R.string.canvas_previous_day),
                            tint = ScribbleFitTheme.colors.midGray
                        )
                    }
                    Text(
                        text = dateString,
                        style = ScribbleFitTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(onClick = onDateClick)
                            .padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = onNextDayClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = stringResource(R.string.canvas_next_day),
                            tint = ScribbleFitTheme.colors.midGray
                        )
                    }
                }
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.canvas_settings_button),
                        tint = ScribbleFitTheme.colors.primary
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CanvasDatePickerDialog(
    initialDate: java.time.LocalDate,
    onDateSelected: (java.time.LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(java.time.ZoneId.systemDefault())
            .toInstant().toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = java.time.Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                return !date.isAfter(java.time.LocalDate.now())
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = java.time.Instant.ofEpochMilli(it)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text(stringResource(R.string.canvas_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = ScribbleFitTheme.colors.surface,
            titleContentColor = ScribbleFitTheme.colors.primary,
            headlineContentColor = ScribbleFitTheme.colors.primary,
            weekdayContentColor = ScribbleFitTheme.colors.primary,
            subheadContentColor = ScribbleFitTheme.colors.primary,
            navigationContentColor = ScribbleFitTheme.colors.primary,
            yearContentColor = ScribbleFitTheme.colors.primary,
            disabledYearContentColor = ScribbleFitTheme.colors.midGray,
            currentYearContentColor = ScribbleFitTheme.colors.primary,
            selectedYearContentColor = ScribbleFitTheme.colors.primary,
            disabledSelectedYearContentColor = ScribbleFitTheme.colors.midGray,
            selectedYearContainerColor = ScribbleFitTheme.colors.surface,
            disabledSelectedYearContainerColor = ScribbleFitTheme.colors.surface,
            dayContentColor = ScribbleFitTheme.colors.primary,
            disabledDayContentColor = ScribbleFitTheme.colors.midGray,
            selectedDayContentColor = ScribbleFitTheme.colors.primary,
            disabledSelectedDayContentColor = ScribbleFitTheme.colors.midGray,
            selectedDayContainerColor = ScribbleFitTheme.colors.surface,
            disabledSelectedDayContainerColor = ScribbleFitTheme.colors.surface,
            todayContentColor = ScribbleFitTheme.colors.primary,
            todayDateBorderColor = ScribbleFitTheme.colors.primary,
            dayInSelectionRangeContentColor = ScribbleFitTheme.colors.primary,
            dayInSelectionRangeContainerColor = ScribbleFitTheme.colors.surface,
            dividerColor = ScribbleFitTheme.colors.primary,
            dateTextFieldColors = TextFieldDefaults.colors(
                focusedTextColor = ScribbleFitTheme.colors.primary,
                unfocusedTextColor = ScribbleFitTheme.colors.primary,
                disabledTextColor = ScribbleFitTheme.colors.midGray,
                errorTextColor = ScribbleFitTheme.colors.dangerRed,
                focusedContainerColor = ScribbleFitTheme.colors.surface,
                unfocusedContainerColor = ScribbleFitTheme.colors.surface,
                disabledContainerColor = ScribbleFitTheme.colors.midGray,
                errorContainerColor = ScribbleFitTheme.colors.surface,
                focusedIndicatorColor = ScribbleFitTheme.colors.primary,
                unfocusedIndicatorColor = ScribbleFitTheme.colors.primary,
                disabledIndicatorColor = ScribbleFitTheme.colors.midGray,
                errorIndicatorColor = ScribbleFitTheme.colors.dangerRed
            )
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.Transparent,
                titleContentColor = ScribbleFitTheme.colors.primary,
                headlineContentColor = ScribbleFitTheme.colors.primary,
                weekdayContentColor = ScribbleFitTheme.colors.primary,
                subheadContentColor = ScribbleFitTheme.colors.primary,
                navigationContentColor = ScribbleFitTheme.colors.primary,
                yearContentColor = ScribbleFitTheme.colors.primary,
                disabledYearContentColor = ScribbleFitTheme.colors.midGray,
                currentYearContentColor = ScribbleFitTheme.colors.primary,
                selectedYearContentColor = ScribbleFitTheme.colors.primary,
                disabledSelectedYearContentColor = ScribbleFitTheme.colors.midGray,
                selectedYearContainerColor = Color.Transparent,
                disabledSelectedYearContainerColor = Color.Transparent,
                dayContentColor = ScribbleFitTheme.colors.primary,
                disabledDayContentColor = ScribbleFitTheme.colors.midGray,
                selectedDayContentColor = ScribbleFitTheme.colors.primary,
                disabledSelectedDayContentColor = ScribbleFitTheme.colors.midGray,
                selectedDayContainerColor = Color.Transparent,
                disabledSelectedDayContainerColor = Color.Transparent,
                todayContentColor = ScribbleFitTheme.colors.primary,
                todayDateBorderColor = ScribbleFitTheme.colors.primary,
                dayInSelectionRangeContentColor = ScribbleFitTheme.colors.primary,
                dayInSelectionRangeContainerColor = Color.Transparent,
                dividerColor = Color.Transparent
            )
        )
    }
}
