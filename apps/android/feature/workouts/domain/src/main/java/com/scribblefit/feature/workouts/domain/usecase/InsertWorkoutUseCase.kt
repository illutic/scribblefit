package com.scribblefit.feature.workouts.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Workout
import com.scribblefit.feature.exercises.domain.usecase.InsertExerciseToWorkoutUseCase
import com.scribblefit.feature.workouts.domain.InvalidWorkoutDateException
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class InsertWorkoutUseCase(
    private val repository: WorkoutRepository,
    private val insertExerciseToWorkoutUseCase: InsertExerciseToWorkoutUseCase,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(workout: Workout): Result<Long> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            validateWorkout(workout)
            val workoutId = repository.saveWorkout(workout)

            workout.exercises.forEach { exercise ->
                insertExerciseToWorkoutUseCase(workoutId, exercise)
            }

            workoutId
        }
    }

    private fun validateWorkout(workout: Workout) {
        if (workout.date < 0) {
            throw InvalidWorkoutDateException()
        }
    }
}