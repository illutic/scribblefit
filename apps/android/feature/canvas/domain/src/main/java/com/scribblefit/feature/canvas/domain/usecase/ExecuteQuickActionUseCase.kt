package com.scribblefit.feature.canvas.domain.usecase

import com.scribblefit.feature.canvas.domain.repository.CanvasRepository

enum class QuickActionType { REPEAT_LAST, REST_DAY, RUN_5K }

class ExecuteQuickActionUseCase(private val canvasRepository: CanvasRepository) {
    suspend fun execute(type: QuickActionType) {
        val rawText = when (type) {
            QuickActionType.REPEAT_LAST -> "Repeat last workout"
            QuickActionType.REST_DAY -> "Rest day"
            QuickActionType.RUN_5K -> "Run 5k"
        }
        canvasRepository.addScribble(rawText)
    }
}
