package com.scribblefit.api.core.plugins

import com.scribblefit.api.features.config.configRoutes
import com.scribblefit.api.features.config.ConfigService
import com.scribblefit.api.features.exercises.ExerciseService
import com.scribblefit.api.features.exercises.exerciseRoutes
import com.scribblefit.api.features.parser.AiParserService
import com.scribblefit.api.features.parser.parserRoutes
import com.scribblefit.api.features.telemetry.TelemetryService
import com.scribblefit.api.features.telemetry.telemetryRoutes
import com.scribblefit.api.features.auth.AuthService
import com.scribblefit.api.features.auth.authRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val configService by inject<ConfigService>()
    val aiParserService by inject<AiParserService>()
    val exerciseService by inject<ExerciseService>()
    val telemetryService by inject<TelemetryService>()
    val authService by inject<AuthService>()
    
    routing {
        get("/") {
            call.respondText("ScribbleFit API is running")
        }
        
        route("/api") {
            configRoutes(configService)
            parserRoutes(aiParserService)
            exerciseRoutes(exerciseService)
            telemetryRoutes(telemetryService)
            authRoutes(authService)
        }
    }
}
