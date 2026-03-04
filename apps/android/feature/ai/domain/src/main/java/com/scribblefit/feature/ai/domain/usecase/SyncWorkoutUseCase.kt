package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.repository.SyncRepository
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncWorkoutUseCase @Inject constructor(
    private val syncRepository: SyncRepository,
    private val secureKeyStorage: SecureKeyStorage,
    private val openAIEngine: LLMEngine, // TODO: Inject factory or multiple engines
    private val proxyEngine: LLMEngine
) {
    suspend operator fun invoke() {
        val pendingItems = syncRepository.getPendingSyncItems().first()
        
        for (item in pendingItems) {
            syncRepository.updateSyncStatus(item.id, com.scribblefit.feature.ai.domain.model.SyncStatus.PROCESSING)
            
            // Priority 1: Personal API Key (BYOK)
            val apiKey = secureKeyStorage.getApiKey()
            val engine = if (apiKey != null) {
                // In a real app, we'd pick based on provider saved in settings
                openAIEngine 
            } else {
                // Priority 2: Managed Proxy
                proxyEngine
            }
            
            val result = engine.parseWorkout(item.rawText)
            
            result.onSuccess { workout ->
                syncRepository.saveParsedWorkout(item.id, workout)
            }.onFailure {
                syncRepository.updateSyncStatus(item.id, com.scribblefit.feature.ai.domain.model.SyncStatus.FAILED)
            }
        }
    }
}
