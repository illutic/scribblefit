package com.scribblefit.feature.settings.domain

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SystemConfig

class UpdateSystemConfigUseCase(
    private val configRepository: ConfigRepository
) {
    suspend operator fun invoke(config: SystemConfig) {
        configRepository.updateConfig(config)
    }
}
