package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.repository.CanvasRepository

/**
 * Orchestrates adding a raw user entry to the feed and triggering background parsing.
 */
class ProcessScribbleUseCase(
    private val canvasRepository: CanvasRepository
) {
    suspend operator fun invoke(rawText: String) {
        canvasRepository.addScribble(rawText)
    }
}
