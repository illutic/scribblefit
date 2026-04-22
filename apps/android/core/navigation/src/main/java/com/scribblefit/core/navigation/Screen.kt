package com.scribblefit.core.navigation

sealed interface Screen {
    val route: String
    val isTop: Boolean

    data object Canvas : Screen {
        override val route: String = "canvas"
        override val isTop: Boolean = true
    }

    data object Insights : Screen {
        override val route: String = "insights"
        override val isTop: Boolean = true
    }

    data object Ledger : Screen {
        override val route: String = "ledger"
        override val isTop: Boolean = true
    }

    data object Settings : Screen {
        override val route: String = "settings"
        override val isTop: Boolean = false
    }

    data class ExerciseDetails(val exerciseName: String) : Screen {
        override val route: String = "exercise_details/$exerciseName"
        override val isTop: Boolean = false
    }

    data class WorkoutExercises(val workoutId: Long) : Screen {
        override val route: String = "workout_exercises/$workoutId"
        override val isTop: Boolean = false
    }
}
