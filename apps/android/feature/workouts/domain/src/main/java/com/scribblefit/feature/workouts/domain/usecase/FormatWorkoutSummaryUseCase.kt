package com.scribblefit.feature.workouts.domain.usecase

class FormatWorkoutSummaryUseCase {
    operator fun invoke(totalVolume: Double): VolumeSummary {
        return if (totalVolume >= 1000) {
            VolumeSummary(
                value = "%.1f".format(totalVolume / 1000.0),
                isKilo = true
            )
        } else {
            VolumeSummary(
                value = "%.0f".format(totalVolume),
                isKilo = false
            )
        }
    }

    data class VolumeSummary(
        val value: String,
        val isKilo: Boolean
    )
}
