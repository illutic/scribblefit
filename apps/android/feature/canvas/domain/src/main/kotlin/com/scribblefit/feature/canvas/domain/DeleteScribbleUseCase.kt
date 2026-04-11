package com.scribblefit.feature.canvas.domain

import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase

class DeleteScribbleUseCase(
    private val removeScribbleUseCase: RemoveScribbleUseCase
) {
    suspend operator fun invoke(id: Long) {
        removeScribbleUseCase(id)
    }
}
