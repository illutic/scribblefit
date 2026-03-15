package com.scribblefit.feature.workouts.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Workout
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.Date

class GetWorkoutByDateUseCase(
    private val repository: WorkoutRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(date: Date): Result<Workout?> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                repository.getWorkoutByDate(date.toInstant().toEpochMilli()).firstOrNull()
            }
        }
}
