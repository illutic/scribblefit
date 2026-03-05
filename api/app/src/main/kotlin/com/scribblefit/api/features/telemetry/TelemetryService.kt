package com.scribblefit.api.features.telemetry

import org.slf4j.LoggerFactory

interface TelemetryService {
    suspend fun reportError(request: TelemetryRequest)
}

class TelemetryServiceImpl : TelemetryService {
    private val logger = LoggerFactory.getLogger("Telemetry")

    override suspend fun reportError(request: TelemetryRequest) {
        // Log formatted failure for prompt engineering analysis
        logger.error("""
            [AI PARSE FAILURE]
            Raw Text: "${request.rawText}"
            Prompt Ver: ${request.promptVersion}
            Error: ${request.errorMessage}
            Code: ${request.errorCode ?: "N/A"}
            Device: ${request.deviceModel ?: "Unknown"}
        """.trimIndent())
        
        // TODO: In production, save to a HallucinationStore (Database)
    }
}
