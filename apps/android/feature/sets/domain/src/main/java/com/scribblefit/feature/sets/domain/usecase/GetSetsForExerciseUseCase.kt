package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.feature.sets.domain.SetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetSetsForExerciseUseCase(
    private val repository: SetRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(workoutExerciseId: Long) = withContext(coroutineDispatcher) {
        runCatchingWithCancellation { repository.getSetsForExercise(workoutExerciseId) }
    }
}
