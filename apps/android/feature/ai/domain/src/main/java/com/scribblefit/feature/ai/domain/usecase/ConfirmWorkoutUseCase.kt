package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.repository.WorkoutSessionRepository

/**
 * Finalizes the parsed workout session into the permanent workout ledger.
 */
class ConfirmWorkoutUseCase(
    private val sessionRepository: WorkoutSessionRepository,
    // Dependency on permanent ledger repository would go here
) {
    suspend operator fun invoke(workout: ParsedWorkout) {
        // Logic to move session to permanent storage
        sessionRepository.clearActiveSession()
    }
}
