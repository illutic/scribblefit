package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.components.ScribbleFitDateRangePickerDialog
import com.scribblefit.core.designsystem.scribbleGlass
import com.scribblefit.feature.ledger.ui.LedgerIntent
import com.scribblefit.feature.ledger.ui.LedgerState
import java.time.ZoneOffset

@Composable
internal fun DatePickerDialog(
    state: LedgerState,
    onIntent: (LedgerIntent) -> Unit
) {
    if (state.showDatePicker) {
        val datePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = state.startDate.atStartOfDay(ZoneOffset.UTC)
                .toInstant().toEpochMilli(),
            initialSelectedEndDateMillis = state.endDate.atStartOfDay(ZoneOffset.UTC).toInstant()
                .toEpochMilli()
        )
        ScribbleFitDateRangePickerDialog(
            state = datePickerState,
            onDismiss = { onIntent(LedgerIntent.HideDatePicker) },
            onConfirm = {
                onIntent(LedgerIntent.HideDatePicker)
                onIntent(
                    LedgerIntent.DateRangeChanged(
                        startDate = datePickerState.selectedStartDateMillis,
                        endDate = datePickerState.selectedEndDateMillis
                    )
                )
            }
        )
    }
}

@Composable
internal fun DateRangePickerButton(
    dateRange: String,
    onDateRangeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Surface(
            onClick = onDateRangeClick,
            shape = RoundedCornerShape(ScribbleFitTheme.spacing.smallLarger),
            modifier = Modifier.scribbleGlass(cornerRadius = ScribbleFitTheme.spacing.smallLarger),
            color = ScribbleFitTheme.colors.surfaceContainerLow
        ) {
            Row(
                modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = null,
                    tint = ScribbleFitTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = dateRange,
                    style = ScribbleFitTheme.typography.bodyMedium,
                    color = ScribbleFitTheme.colors.primary
                )
            }
        }
    }
}