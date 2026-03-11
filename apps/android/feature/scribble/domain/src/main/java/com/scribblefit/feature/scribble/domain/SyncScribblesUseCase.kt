package com.scribblefit.feature.scribble.domain

import com.scribblefit.feature.ai.domain.engine.LLMEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

class SyncScribblesUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val engine: LLMEngine,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    private val logger = LoggerFactory.getLogger("SyncScribblesUseCase")
    suspend operator fun invoke() = withContext(coroutineDispatcher) {
        scribbleRepository.getPendingScribbles().collect { scribbles ->
            for (item in scribbles) {
                val rawText = item.rawText

                engine.parseWorkout(rawText)
                    .fold(
                        onSuccess = { result ->
                            this@SyncScribblesUseCase.scribbleRepository.updateSyncStatus(
                                item.id,
                                SyncStatus.Completed(result)
                            ).fold(
                                onSuccess = {
                                    logger.info("Successfully parsed workout for item ${item.id}")
                                },
                                onFailure = { error ->
                                    logger.error("Error updating sync status for item ${item.id}: ${error.message}")
                                }
                            )
                        },
                        onFailure = { error ->
                            logger.error("Error parsing workout for item ${item.id}: ${error.message}")
                            scribbleRepository.updateSyncStatus(
                                item.id,
                                SyncStatus.Failed
                            )
                        }
                    )
            }
        }
    }
}
