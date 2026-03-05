package com.scribblefit.api.features.telemetry

import org.slf4j.LoggerFactory

interface TelemetryService {
    suspend fun reportError(request: TelemetryRequest)
}

class TelemetryServiceImpl(
    private val repository: TelemetryRepository
) : TelemetryService {
    private val logger = LoggerFactory.getLogger("Telemetry")

    override suspend fun reportError(request: TelemetryRequest) {
        // 1. Log locally for immediate visibility
        logger.error("""
            [AI PARSE FAILURE]
            Raw Text: "${request.rawText}"
            Prompt Ver: ${request.promptVersion}
            Error: ${request.errorMessage}
            Code: ${request.errorCode ?: "N/A"}
        """.trimIndent())
        
        // 2. Persist to data store (e.g. Supabase in the future)
        repository.saveError(request)
    }
}
