package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.engine.SyncRepository
import kotlinx.coroutines.flow.collectLatest

class ListenForSyncItemsUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke() {
        syncRepository.getPendingSyncItems().collectLatest {
            syncRepository.syncWorkouts()
        }
    }
}