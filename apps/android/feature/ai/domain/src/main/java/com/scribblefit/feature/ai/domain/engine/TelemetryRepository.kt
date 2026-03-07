package com.scribblefit.feature.ai.domain.engine

import com.scribblefit.feature.ai.domain.model.TelemetryData

interface TelemetryRepository {
    suspend fun reportError(data: TelemetryData): Result<Unit>
}
