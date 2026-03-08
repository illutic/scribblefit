package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.engine.ConfigRepository
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.engine.SyncRepository
import com.scribblefit.feature.ai.domain.engine.TelemetryRepository
import com.scribblefit.feature.ai.domain.model.ParsingStatus
import com.scribblefit.feature.ai.domain.model.SyncStatus
import com.scribblefit.feature.ai.domain.model.TelemetryData
import kotlinx.coroutines.flow.first

class SyncWorkoutUseCase(
    private val syncRepository: SyncRepository,
    private val telemetryRepository: TelemetryRepository,
    private val engine: LLMEngine,
    private val configRepository: ConfigRepository
) {
    suspend operator fun invoke() {
        val config = configRepository.getConfig().first()
        val promptVersion = config?.promptVersion ?: "1.0.0"

        val pendingItems = syncRepository.getPendingSyncItems().first()

        for (item in pendingItems) {
            syncRepository.updateSyncStatus(item.id, SyncStatus.PROCESSING)

            val result = engine.parseWorkout(item.rawText)

            if (result.status == ParsingStatus.SUCCESS && result.workout != null) {
                syncRepository.saveParsedWorkout(item.id, result.workout)
            } else {
                syncRepository.updateSyncStatus(item.id, SyncStatus.FAILED)

                telemetryRepository.reportError(
                    TelemetryData(
                        rawText = item.rawText,
                        promptVersion = promptVersion,
                        errorMessage = result.error ?: "Unknown error during parsing"
                    )
                )
            }
        }
    }
}
