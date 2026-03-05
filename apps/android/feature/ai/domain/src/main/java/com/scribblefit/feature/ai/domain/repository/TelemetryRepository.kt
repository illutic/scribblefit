package com.scribblefit.feature.ai.domain.repository

import com.scribblefit.feature.ai.domain.model.TelemetryData

interface TelemetryRepository {
    suspend fun reportError(data: TelemetryData): Result<Unit>
}
