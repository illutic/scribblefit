package com.scribblefit.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.*

class ConfigTest {
    @Test
    fun testGetPrompt() = testApplication {
        environment {
            config = MapApplicationConfig(
                "scribblefit.config.promptVersion" to "1.0.0",
                "scribblefit.config.promptText" to "Test Prompt"
            )
        }
        application {
            module()
        }
        val response = client.get("/api/config/prompt")
        assertEquals(HttpStatusCode.OK, response.status)
        
        val body = response.bodyAsText()
        val json = Json.parseToJsonElement(body).jsonObject
        
        assertEquals("1.0.0", json["version"]?.jsonPrimitive?.content)
        assertEquals("Test Prompt", json["prompt"]?.jsonPrimitive?.content)
    }

    @Test
    fun testGetMetadata() = testApplication {
        environment {
            config = MapApplicationConfig(
                "scribblefit.version" to "1.2.3"
            )
        }
        application {
            module()
        }
        val response = client.get("/api/sync/metadata")
        assertEquals(HttpStatusCode.OK, response.status)
        
        val body = response.bodyAsText()
        val json = Json.parseToJsonElement(body).jsonObject
        
        assertEquals("ok", json["status"]?.jsonPrimitive?.content)
        assertEquals("1.2.3", json["version"]?.jsonPrimitive?.content)
    }
}
