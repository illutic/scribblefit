package com.scribblefit.api

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class TelemetryTest {
    @Test
    fun testReportError() = testApplication {
        application {
            module()
        }
        val response = client.post("/api/telemetry/errors") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""
                {
                    "rawText": "bench 500x100",
                    "promptVersion": "1.0.0",
                    "errorMessage": "Impossible weight",
                    "deviceModel": "Emulator"
                }
            """.trimIndent())
        }
        assertEquals(HttpStatusCode.Accepted, response.status)
    }
}
