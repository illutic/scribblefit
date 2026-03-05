package com.scribblefit.api.features.auth

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/login") {
            val request = call.receive<AuthRequest>()
            val response = authService.authenticate(request)
            call.respond(response)
        }
    }
}
