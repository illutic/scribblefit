package com.scribblefit.feature.workouts.domain.usecase

import com.scribblefit.core.model.Workout

class CalculateWorkoutVolumeUseCase {
    operator fun invoke(workout: Workout): Double {
        return workout.exercises.sumOf { exercise ->
            exercise.sets.sumOf { set ->
                (set.weight?.toDouble() ?: 0.0) * set.reps
            }
        }
    }
}
