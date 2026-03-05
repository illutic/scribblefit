package com.scribblefit.api.features.parser

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.parserRoutes(aiParserService: AiParserService) {
    route("/parse") {
        authenticate("auth-jwt") {
            post("/proxy") {
                val request = call.receive<ParseRequest>()
                
                try {
                    val parsedWorkout = aiParserService.parseWorkout(
                        rawText = request.rawText,
                        prompt = request.prompt
                    )
                    call.respond(parsedWorkout)
                } catch (e: Exception) {
                    throw e
                }
            }
        }
    }
}
