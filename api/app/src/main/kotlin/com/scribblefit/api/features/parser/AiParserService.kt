package com.scribblefit.api.features.parser

interface AiParserService {
    suspend fun parseWorkout(rawText: String, prompt: String? = null): ParsedWorkoutDto
}
