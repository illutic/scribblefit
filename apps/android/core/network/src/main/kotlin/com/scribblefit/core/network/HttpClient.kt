package com.scribblefit.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun defaultJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
}

fun buildBaseHttpClient(json: Json) = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(json)
    }
    install(Logging) {
        logger = Logger.ANDROID
        level = LogLevel.ALL
    }
}
