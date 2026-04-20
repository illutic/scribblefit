package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.feature.sets.domain.SetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateSetWeightUseCase(
    private val repository: SetRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(setId: Long, weight: Float?): Result<Unit> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            repository.updateSetWeight(setId, weight)
        }
    }
}
