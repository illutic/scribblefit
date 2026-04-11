package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.feature.sets.domain.SetRepository

class RemoveSetUseCase(
    private val setRepository: SetRepository
) {
    suspend operator fun invoke(setId: Long) {
        setRepository.deleteSet(setId)
    }
}
