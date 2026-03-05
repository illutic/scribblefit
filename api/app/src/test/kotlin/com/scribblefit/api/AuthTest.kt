package com.scribblefit.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.*

class AuthTest {
    @Test
    fun testLogin() = testApplication {
        application {
            module()
        }
        val response = client.post("/api/auth/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"deviceId": "test-device"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        
        val body = response.bodyAsText()
        val json = Json.parseToJsonElement(body).jsonObject
        
        assertNotNull(json["token"]?.jsonPrimitive?.content)
        assertNotNull(json["expiresAt"]?.jsonPrimitive?.content)
    }
}
