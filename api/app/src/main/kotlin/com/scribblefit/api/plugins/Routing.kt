package com.scribblefit.api.plugins

import com.scribblefit.api.routes.configRoutes
import com.scribblefit.api.services.ConfigService
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
