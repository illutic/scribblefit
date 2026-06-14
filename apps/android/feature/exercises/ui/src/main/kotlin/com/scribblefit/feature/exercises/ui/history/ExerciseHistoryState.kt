package com.scribblefit.feature.exercises.ui.history

import com.scribblefit.core.model.ExerciseHistorySession
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ExerciseHistoryState(
    val exerciseName: String = "",
    val isLoading: Boolean = true,
    val history: List<ExerciseHistorySession> = emptyList(),
    val error: String? = null
) {
    val groupedHistory: Map<String, List<ExerciseHistorySession>>
        get() {
            val formatter = DateTimeFormatter.ofPattern(
                "MMMM yyyy",
                Locale.getDefault()
            )
            return history.groupBy { session ->
                val instant = Instant.ofEpochMilli(session.date)
                val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
                localDate.format(formatter).uppercase()
            }
        }
}
