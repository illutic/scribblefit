package com.scribblefit.core.navigation

sealed class Screen(val route: String) {
    data object Canvas : Screen("canvas")
    data object Insights : Screen("insights")
    data object Ledger : Screen("ledger")
    data object Profile : Screen("profile")
}
