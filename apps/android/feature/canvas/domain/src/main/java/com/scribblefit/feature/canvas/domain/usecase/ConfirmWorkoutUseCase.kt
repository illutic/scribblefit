package com.scribblefit.feature.canvas.domain.usecase

import com.scribblefit.core.ai.model.ParsedWorkout
import com.scribblefit.feature.canvas.domain.repository.WorkoutSessionRepository

class ConfirmWorkoutUseCase(
    private val sessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(workout: ParsedWorkout) {
        sessionRepository.clearActiveSession()
    }
}
