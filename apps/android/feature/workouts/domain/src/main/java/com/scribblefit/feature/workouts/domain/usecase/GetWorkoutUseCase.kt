package com.scribblefit.feature.workouts.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Workout
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class GetWorkoutUseCase(
    private val repository: WorkoutRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(id: Long): Result<Workout?> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                repository.getWorkoutById(id).firstOrNull()
            }
        }
}
