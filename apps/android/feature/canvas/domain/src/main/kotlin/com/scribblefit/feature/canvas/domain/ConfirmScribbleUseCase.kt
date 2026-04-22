package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Workout
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.scribble.domain.error.ScribbleError
import com.scribblefit.feature.workouts.domain.WorkoutRepository

class ConfirmScribbleUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(scribble: Scribble): Result<Unit> = runCatching {
        if (scribble.status != ScribbleStatus.SUCCESS) {
            throw ScribbleError.InvalidStatus(scribble.status)
        }

        // 1. Create the workout domain model
        val workout = Workout(
            id = 0,
            date = scribble.createdAt,
            exercises = scribble.exercises, // Link current state of exercises
            notes = listOf("Imported from scribble: ${scribble.rawText}")
        )

        // 2. Atomically confirm (clear, save workout, link, update status)
        scribbleRepository.confirmScribble(scribble, workout)
    }
}
