package com.scribblefit.api.features.parser

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.parserRoutes(aiParserService: AiParserService) {
    route("/parse") {
        post("/proxy") {
            // TODO: Add Auth validation (JWT or Subscription)
            val request = call.receive<ParseRequest>()
            
            try {
                val parsedWorkout = aiParserService.parseWorkout(
                    rawText = request.rawText,
                    prompt = request.prompt
                )
                call.respond(parsedWorkout)
            } catch (e: Exception) {
                // StatusPages plugin should handle this, but let's be explicit for now if needed
                throw e
            }
        }
    }
}
