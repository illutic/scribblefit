package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.core.ai.engine.LLMEngine
import com.scribblefit.core.ai.model.AIParsingException
import com.scribblefit.core.ai.model.SyncStatus
import com.scribblefit.core.ai.model.TelemetryData
import com.scribblefit.core.ai.engine.SyncRepository
import com.scribblefit.core.ai.engine.TelemetryRepository
import kotlinx.coroutines.flow.first

class SyncWorkoutUseCase(
    private val syncRepository: SyncRepository,
    private val telemetryRepository: TelemetryRepository,
    private val engine: LLMEngine,
    private val promptVersion: String
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
