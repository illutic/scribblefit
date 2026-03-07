package com.scribblefit.feature.canvas.domain.usecase

import com.scribblefit.feature.canvas.domain.repository.CanvasRepository

/**
 * Maps minimalist UI actions (e.g., "Repeat Last") to specific scribble injections.
 */
class ExecuteQuickActionUseCase(
    private val canvasRepository: CanvasRepository
) {
    suspend operator fun invoke(actionType: QuickActionType) {
        val scribbleText = when (actionType) {
            QuickActionType.REPEAT_LAST -> "Repeat last workout"
            QuickActionType.REST_DAY -> "Today is a rest day"
            QuickActionType.RUN_5K -> "Logged a 5k run"
        }
        canvasRepository.addScribble(scribbleText)
    }
}

enum class QuickActionType {
    REPEAT_LAST, REST_DAY, RUN_5K
}
