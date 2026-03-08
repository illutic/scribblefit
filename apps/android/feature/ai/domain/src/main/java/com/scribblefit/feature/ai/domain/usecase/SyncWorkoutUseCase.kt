package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.engine.SyncRepository
import com.scribblefit.feature.ai.domain.model.ParsingStatus
import com.scribblefit.feature.ai.domain.model.SyncStatus
import kotlinx.coroutines.flow.first

class SyncWorkoutUseCase(
    private val syncRepository: SyncRepository,
    private val engine: LLMEngine
) {
    suspend operator fun invoke() {
        val pendingItems = syncRepository.getPendingSyncItems().first()
        for (item in pendingItems) {
            val rawText = item.rawText ?: continue
            syncRepository.updateSyncStatus(item.id, SyncStatus.PROCESSING)
            val result = engine.parseWorkout(rawText)
            if (result.status == ParsingStatus.SUCCESS && result.workout != null) {
                syncRepository.saveParsedWorkout(item.id, result.workout)
            } else {
                syncRepository.updateSyncStatus(item.id, SyncStatus.FAILED)
            }
        }
    }
}
