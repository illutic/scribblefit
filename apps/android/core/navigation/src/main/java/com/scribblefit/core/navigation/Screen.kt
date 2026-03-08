package com.scribblefit.core.navigation

sealed class Screen(val route: String) {
    data object Canvas : Screen("canvas")
    data object Ledger : Screen("ledger")
    data object Analytics : Screen("analytics")
    data object Settings : Screen("settings")
    data object ExerciseLibrary : Screen("exercise_library")
}
