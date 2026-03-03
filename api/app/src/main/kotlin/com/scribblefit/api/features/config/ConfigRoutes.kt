package com.scribblefit.api.features.config

import com.scribblefit.api.features.config.ConfigService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configRoutes(configService: ConfigService) {
    route("/config") {
        get("/prompt") {
            val config = configService.getPromptConfig()
            call.respond(config)
        }
    }
    
    route("/sync") {
        get("/metadata") {
            val metadata = configService.getMetadata()
            call.respond(metadata)
        }
    }
}
