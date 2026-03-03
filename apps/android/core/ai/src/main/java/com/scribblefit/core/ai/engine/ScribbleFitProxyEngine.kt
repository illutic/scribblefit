package com.scribblefit.core.ai.engine

import com.scribblefit.core.ai.model.ParsedExercise
import com.scribblefit.core.ai.model.ParsedSet
import com.scribblefit.core.ai.model.ParsedWorkout
import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.ParseRequest
import com.scribblefit.core.network.model.ParsedWorkoutDto
import javax.inject.Inject

class ScribbleFitProxyEngine @Inject constructor(
    private val api: ScribbleFitApi,
    private val systemPrompt: String
) : LLMEngine {
    
    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> {
        return try {
            val request = ParseRequest(
                rawText = rawText,
                prompt = systemPrompt
            )
            val responseDto = api.parseProxy(request)
            Result.success(mapToDomain(responseDto))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapToDomain(dto: ParsedWorkoutDto): ParsedWorkout {
        return ParsedWorkout(
            date = dto.date,
            location = dto.location,
            exercises = dto.exercises.map { exerciseDto ->
                ParsedExercise(
                    canonicalName = exerciseDto.canonicalName,
                    sets = exerciseDto.sets.map { setDto ->
                        ParsedSet(
                            weight = setDto.weight,
                            reps = setDto.reps,
                            rpe = setDto.rpe,
                            notes = setDto.notes
                        )
                    }
                )
            }
        )
    }
}
