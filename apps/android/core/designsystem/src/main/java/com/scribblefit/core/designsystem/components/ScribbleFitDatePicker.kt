package com.scribblefit.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scribblefit.core.designsystem.ScribbleFitTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun scribbleFitDatePickerColors() = DatePickerDefaults.colors(
    containerColor = ScribbleFitTheme.colors.background,
    titleContentColor = ScribbleFitTheme.colors.primary,
    headlineContentColor = ScribbleFitTheme.colors.primary,
    weekdayContentColor = ScribbleFitTheme.colors.primary,
    subheadContentColor = ScribbleFitTheme.colors.primary,
    navigationContentColor = ScribbleFitTheme.colors.primary,
    yearContentColor = ScribbleFitTheme.colors.primary,
    disabledYearContentColor = ScribbleFitTheme.colors.midGray,
    currentYearContentColor = ScribbleFitTheme.colors.primary,
    selectedYearContentColor = ScribbleFitTheme.colors.onPrimary,
    disabledSelectedYearContentColor = ScribbleFitTheme.colors.midGray,
    selectedYearContainerColor = ScribbleFitTheme.colors.primary,
    disabledSelectedYearContainerColor = ScribbleFitTheme.colors.surface,
    dayContentColor = ScribbleFitTheme.colors.primary,
    disabledDayContentColor = ScribbleFitTheme.colors.midGray,
    selectedDayContentColor = ScribbleFitTheme.colors.onPrimary,
    disabledSelectedDayContentColor = ScribbleFitTheme.colors.midGray,
    selectedDayContainerColor = ScribbleFitTheme.colors.primary,
    disabledSelectedDayContainerColor = ScribbleFitTheme.colors.surface,
    todayContentColor = ScribbleFitTheme.colors.primary,
    todayDateBorderColor = ScribbleFitTheme.colors.primary,
    dayInSelectionRangeContentColor = ScribbleFitTheme.colors.onPrimary,
    dayInSelectionRangeContainerColor = ScribbleFitTheme.colors.primary,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScribbleFitDatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "OK",
    cancelText: String = "Cancel"
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                return !date.isAfter(LocalDate.now())
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        },
        colors = scribbleFitDatePickerColors()
    ) {
        DatePicker(
            state = datePickerState,
            colors = scribbleFitDatePickerColors()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScribbleFitDateRangePickerDialog(
    state: DateRangePickerState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "OK",
    cancelText: String = "Cancel",
    headerText: String = "Select Dates",
    modifier: Modifier = Modifier
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        },
        colors = scribbleFitDatePickerColors()
    ) {
        DateRangePicker(
            state = state,
            modifier = modifier,
            headline = {
                Text(
                    text = headerText,
                    style = ScribbleFitTheme.typography.headlineSmall,
                    color = ScribbleFitTheme.colors.primary,
                    modifier = Modifier.padding(horizontal = ScribbleFitTheme.spacing.medium)
                )
            },
            title = {},
            colors = scribbleFitDatePickerColors()
        )
    }
}
