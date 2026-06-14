package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetExercisesInRangeUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        startTime: CurrentDate,
        endTime: CurrentDate
    ) = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            exerciseRepository.getExercisesInRange(
                startTime.millis,
                endTime.millis
            )
        }
    }
}