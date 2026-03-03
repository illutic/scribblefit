package com.scribblefit.api

import com.scribblefit.api.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureFirebase()
    configureSerialization()
    configureCORS()
    configureRouting()
}
