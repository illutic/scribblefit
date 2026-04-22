package com.scribblefit.feature.workouts.domain.usecase

import com.scribblefit.core.model.Workout
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetWorkoutWithExercisesUseCase(
    private val repository: WorkoutRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(workoutId: Long): Flow<Workout?> =
        repository.getWorkoutById(workoutId)
            .flowOn(coroutineDispatcher)
}
