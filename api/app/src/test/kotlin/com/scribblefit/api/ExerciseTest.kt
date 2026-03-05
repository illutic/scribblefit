package com.scribblefit.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.*
import kotlin.test.*

class ExerciseTest {
    @Test
    fun testGetExercises() = testApplication {
        application {
            module()
        }
        val response = client.get("/api/sync/exercises")
        assertEquals(HttpStatusCode.OK, response.status)
        
        val body = response.bodyAsText()
        val json = Json.parseToJsonElement(body).jsonObject
        val exercises = json["exercises"]?.jsonArray
        
        assertNotNull(exercises)
        assertTrue(exercises.size > 0)
        
        val firstExercise = exercises[0].jsonObject
        assertNotNull(firstExercise["canonicalName"])
        assertNotNull(firstExercise["muscleGroup"])
    }
}
