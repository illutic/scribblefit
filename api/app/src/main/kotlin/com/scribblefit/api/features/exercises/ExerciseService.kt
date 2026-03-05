package com.scribblefit.api.features.exercises

interface ExerciseService {
    suspend fun getExercises(): List<ExerciseDto>
    fun getExerciseVersion(): String
}

class ExerciseServiceImpl : ExerciseService {
    // Initial seeded exercise list
    private val exercises = listOf(
        ExerciseDto("1", "Bench Press", "Chest", listOf("bench", "chest press", "flat bench")),
        ExerciseDto("2", "Squat", "Legs", listOf("back squat", "goblet squat")),
        ExerciseDto("3", "Deadlift", "Back", listOf("conventional deadlift", "deadlift")),
        ExerciseDto("4", "Overhead Press", "Shoulders", listOf("ohp", "military press", "shoulder press")),
        ExerciseDto("5", "Barbell Row", "Back", listOf("bent over row", "rows"))
    )

    override suspend fun getExercises(): List<ExerciseDto> = exercises
    
    override fun getExerciseVersion(): String = "1.0.0"
}
