package com.scribblefit.api.features.exercises

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.exerciseRoutes(exerciseService: ExerciseService) {
    route("/sync") {
        get("/exercises") {
            val exercises = exerciseService.getExercises()
            call.respond(ExerciseResponse(exercises))
        }
    }
}
