package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetExerciseByIdUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(exerciseId: Long): Result<Exercise> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                exerciseRepository.getExerciseById(exerciseId) ?: error("Exercise not found")
            }
        }
}