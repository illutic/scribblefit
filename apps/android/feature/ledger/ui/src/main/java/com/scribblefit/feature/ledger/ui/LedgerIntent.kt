package com.scribblefit.feature.ledger.ui

import java.time.LocalDate

sealed interface LedgerIntent {
    data class DateRangeChanged(val startDate: LocalDate, val endDate: LocalDate) : LedgerIntent
    data class WorkoutClicked(val workoutId: Long) : LedgerIntent
}
