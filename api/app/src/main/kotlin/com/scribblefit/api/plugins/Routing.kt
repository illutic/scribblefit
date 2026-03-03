package com.scribblefit.api.plugins

import com.scribblefit.api.routes.configRoutes
import com.scribblefit.api.services.ConfigService
import com.scribblefit.api.services.ConfigServiceImpl
import com.scribblefit.api.services.FirebaseConfigServiceImpl
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val useFirebase = environment.config.propertyOrNull("scribblefit.firebase.enabled")?.getString()?.toBoolean() ?: false
    
    val configService: ConfigService = if (useFirebase) {
        FirebaseConfigServiceImpl()
    } else {
        ConfigServiceImpl(environment.config)
    }
    
    routing {
        get("/") {
            call.respondText("ScribbleFit API is running")
        }
        
        route("/api") {
            configRoutes(configService)
            
            // Existing sync routes
            route("/sync") {
                get("/metadata") {
                    call.respond(mapOf("status" to "ok", "version" to "1.0.0"))
                }
            }
        }
    }
}
