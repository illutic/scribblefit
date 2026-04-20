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

        // 1. Persist any UI edits made by the user before confirming
        scribbleRepository.clearScribbleExercises(scribble.id)
        scribbleRepository.saveScribbleExercises(scribble.id, scribble.exercises)

        val workout = Workout(
            id = 0,
            date = scribble.createdAt,
            exercises = emptyList(),
            notes = listOf("Imported from scribble: ${scribble.rawText}")
        )

        // 2. Create the workout and link existing exercises
        val workoutId = workoutRepository.saveWorkout(workout)
        scribbleRepository.updateScribbleExercisesToWorkout(scribble.id, workoutId)

        // 3. Mark as completed
        scribbleRepository.updateScribble(scribble.copy(status = ScribbleStatus.COMPLETED))
    }
}
