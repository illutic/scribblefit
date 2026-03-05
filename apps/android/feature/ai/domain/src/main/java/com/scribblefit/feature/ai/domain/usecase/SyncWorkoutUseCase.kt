package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AIParsingException
import com.scribblefit.feature.ai.domain.model.SyncStatus
import com.scribblefit.feature.ai.domain.model.TelemetryData
import com.scribblefit.feature.ai.domain.repository.SyncRepository
import com.scribblefit.feature.ai.domain.repository.TelemetryRepository
import kotlinx.coroutines.flow.first

class SyncWorkoutUseCase(
    private val syncRepository: SyncRepository,
    private val telemetryRepository: TelemetryRepository,
    private val engine: LLMEngine,
    private val promptVersion: String // Injected prompt version for telemetry
) {
    suspend operator fun invoke() {
        val pendingItems = syncRepository.getPendingSyncItems().first()
        
        for (item in pendingItems) {
            syncRepository.updateSyncStatus(item.id, SyncStatus.PROCESSING)
            
            val result = engine.parseWorkout(item.rawText)
            
            result.onSuccess { workout ->
                syncRepository.saveParsedWorkout(item.id, workout)
            }.onFailure { error ->
                syncRepository.updateSyncStatus(item.id, SyncStatus.FAILED)
                
                val errorMessage = when (error) {
                    is AIParsingException -> error.error
                    else -> error.message ?: "Unknown error during parsing"
                }

                // Report failure to telemetry
                telemetryRepository.reportError(
                    TelemetryData(
                        rawText = item.rawText,
                        promptVersion = promptVersion,
                        errorMessage = errorMessage
                    )
                )
            }
        }
    }
}
