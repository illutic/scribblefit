package com.scribblefit.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.*

class ConfigTest {
    @Test
    fun testGetPrompt() = testApplication {
        application {
            module()
        }
        val response = client.get("/api/config/prompt")
        assertEquals(HttpStatusCode.OK, response.status)
        
        val body = response.bodyAsText()
        val json = Json.parseToJsonElement(body).jsonObject
        
        assertTrue(json.containsKey("version"))
        assertTrue(json.containsKey("prompt"))
        assertEquals("1.0.0", json["version"]?.jsonPrimitive?.content)
    }
}
