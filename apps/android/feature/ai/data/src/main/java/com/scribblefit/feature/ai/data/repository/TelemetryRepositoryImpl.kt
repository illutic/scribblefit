package com.scribblefit.feature.ai.data.repository

import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.TelemetryRequest
import com.scribblefit.feature.ai.domain.model.TelemetryData
import com.scribblefit.feature.ai.domain.repository.TelemetryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelemetryRepositoryImpl @Inject constructor(
    private val api: ScribbleFitApi
) : TelemetryRepository {

    override suspend fun reportError(data: TelemetryData): Result<Unit> = runCatching {
        api.reportError(
            TelemetryRequest(
                rawText = data.rawText,
                promptVersion = data.promptVersion,
                errorMessage = data.errorMessage,
                errorCode = data.errorCode,
                deviceModel = data.deviceModel
            )
        )
        Unit
    }
}
