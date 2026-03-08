package com.scribblefit.feature.canvas.domain.usecase

import com.scribblefit.feature.canvas.domain.repository.CanvasRepository

class ProcessScribbleUseCase(private val canvasRepository: CanvasRepository) {
    suspend fun execute(rawText: String) {
        canvasRepository.addScribble(rawText)
    }
}
