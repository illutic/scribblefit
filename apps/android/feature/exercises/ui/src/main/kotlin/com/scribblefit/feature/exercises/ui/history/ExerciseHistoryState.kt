package com.scribblefit.feature.exercises.ui.history

import com.scribblefit.core.model.ExerciseHistorySession

data class ExerciseHistoryState(
    val exerciseName: String = "",
    val isLoading: Boolean = true,
    val history: List<ExerciseHistorySession> = emptyList(),
    val error: String? = null
) {
    val groupedHistory: Map<String, List<ExerciseHistorySession>>
        get() {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.getDefault())
            return history.groupBy { session ->
                val instant = java.time.Instant.ofEpochMilli(session.date)
                val localDate = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                localDate.format(formatter).uppercase()
            }
        }
}
