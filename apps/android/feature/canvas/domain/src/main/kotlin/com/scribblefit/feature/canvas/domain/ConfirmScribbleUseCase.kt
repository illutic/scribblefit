package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Workout
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.workouts.domain.usecase.InsertWorkoutUseCase

class ConfirmScribbleUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val insertWorkoutUseCase: InsertWorkoutUseCase,
) {
    suspend operator fun invoke(scribble: Scribble) {
        if (scribble.status != ScribbleStatus.SUCCESS) return

        val workout = Workout(
            id = 0,
            date = scribble.createdAt,
            exercises = scribble.exercises,
            notes = listOf("Imported from scribble: ${scribble.rawText}")
        )

        insertWorkoutUseCase(workout)

        scribbleRepository.updateScribble(scribble.copy(status = ScribbleStatus.COMPLETED))
    }
}
