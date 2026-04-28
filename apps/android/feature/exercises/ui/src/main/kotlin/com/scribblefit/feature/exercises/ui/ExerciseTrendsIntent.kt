package com.scribblefit.feature.exercises.ui

import com.scribblefit.feature.exercises.domain.usecase.TrendPeriod

sealed interface ExerciseTrendsIntent {
    data class UpdatePeriod(val period: TrendPeriod) : ExerciseTrendsIntent
    data object NavigateBack : ExerciseTrendsIntent
}
