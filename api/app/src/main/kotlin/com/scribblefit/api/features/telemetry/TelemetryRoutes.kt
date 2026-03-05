package com.scribblefit.api.features.telemetry

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.telemetryRoutes(telemetryService: TelemetryService) {
    route("/telemetry") {
        post("/errors") {
            val request = call.receive<TelemetryRequest>()
            telemetryService.reportError(request)
            call.respond(io.ktor.http.HttpStatusCode.Accepted)
        }
    }
}
