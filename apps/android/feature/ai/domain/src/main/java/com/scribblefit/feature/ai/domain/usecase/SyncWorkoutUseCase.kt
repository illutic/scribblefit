package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.repository.SyncRepository
import kotlinx.coroutines.flow.first

class SyncWorkoutUseCase(
    private val syncRepository: SyncRepository,
    private val engine: LLMEngine
) {
    suspend operator fun invoke() {
        val pendingItems = syncRepository.getPendingSyncItems().first()
        
        for (item in pendingItems) {
            syncRepository.updateSyncStatus(item.id, com.scribblefit.feature.ai.domain.model.SyncStatus.PROCESSING)
            
            val result = engine.parseWorkout(item.rawText)
            
            result.onSuccess { workout ->
                syncRepository.saveParsedWorkout(item.id, workout)
            }.onFailure {
                syncRepository.updateSyncStatus(item.id, com.scribblefit.feature.ai.domain.model.SyncStatus.FAILED)
            }
        }
    }
}
