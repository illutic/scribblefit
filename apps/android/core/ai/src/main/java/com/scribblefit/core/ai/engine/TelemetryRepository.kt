package com.scribblefit.core.ai.engine

import com.scribblefit.core.ai.model.TelemetryData

interface TelemetryRepository {
    suspend fun reportError(data: TelemetryData): Result<Unit>
}
