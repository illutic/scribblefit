package com.scribblefit.feature.ledger.ui

import com.scribblefit.core.navigation.Screen

sealed interface LedgerIntent {
    data class DateRangeChanged(val startDate: Long?, val endDate: Long?) : LedgerIntent
    data object ShowDatePicker : LedgerIntent
    data object HideDatePicker : LedgerIntent
    data object Refresh : LedgerIntent
    data class NavigateToScreen(val screen: Screen) : LedgerIntent
}
