package com.scribblefit.api.core.plugins

import com.scribblefit.api.features.config.configRoutes
import com.scribblefit.api.features.config.ConfigService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val configService by inject<ConfigService>()
    
    routing {
        get("/") {
            call.respondText("ScribbleFit API is running")
        }
        
        route("/api") {
            configRoutes(configService)
        }
    }
}
