package com.scribblefit.api.features.telemetry

interface TelemetryRepository {
    suspend fun saveError(request: TelemetryRequest)
}

class LoggingTelemetryRepositoryImpl : TelemetryRepository {
    private val logger = org.slf4j.LoggerFactory.getLogger("TelemetryRepo")

    override suspend fun saveError(request: TelemetryRequest) {
        // Placeholder for future Supabase/Database implementation
        logger.info("Telemetry saved to log: ${request.errorMessage}")
    }
}
