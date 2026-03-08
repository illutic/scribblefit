package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.engine.SyncRepository
import com.scribblefit.feature.ai.domain.model.SyncStatus

class ListenForSyncItemsUseCase(private val syncRepository: SyncRepository) {
    suspend operator fun invoke() {
        syncRepository.getAllSyncItems().collect { items ->
            if (items.any { it.status == SyncStatus.PENDING }) {
                syncRepository.syncWorkouts()
            }
        }
    }
}
