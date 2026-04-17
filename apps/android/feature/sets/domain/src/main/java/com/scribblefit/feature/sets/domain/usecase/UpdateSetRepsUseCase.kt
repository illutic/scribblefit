package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.feature.sets.domain.SetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateSetRepsUseCase(
    private val repository: SetRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(setId: Long, reps: Int): Result<Unit> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            repository.updateSetReps(setId, reps)
        }
    }
}
