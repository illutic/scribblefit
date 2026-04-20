package com.scribblefit.feature.workouts.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Workout
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateWorkoutUseCase(
    private val workoutRepository: WorkoutRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(workout: Workout): Result<Unit> =
        runCatchingWithCancellation {
            withContext(coroutineDispatcher) {
                workoutRepository.updateWorkout(workout)
            }
        }
}
