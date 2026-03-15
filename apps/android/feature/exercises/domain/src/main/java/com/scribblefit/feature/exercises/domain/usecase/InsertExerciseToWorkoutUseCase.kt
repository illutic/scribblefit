package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.ExerciseNameNotValidException
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.exercises.domain.ExerciseSetsNotValidException
import com.scribblefit.feature.sets.domain.usecase.InsertSetToExerciseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class InsertExerciseToWorkoutUseCase(
    private val repository: ExerciseRepository,
    private val insertSetToExerciseUseCase: InsertSetToExerciseUseCase,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(workoutId: Long, exercise: Exercise): Result<Long> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                validateExercise(exercise)
                val exerciseId = repository.addExercise(exercise.copy(isDraft = true))
                val workoutExerciseId = repository.addExerciseToWorkout(workoutId, exerciseId)
                addSetsToExercise(workoutExerciseId, exercise.sets)

                workoutExerciseId
            }
        }

    private fun validateExercise(exercise: Exercise) {
        when {
            exercise.canonicalName.isBlank() -> throw ExerciseNameNotValidException()
            exercise.sets.isEmpty() -> throw ExerciseSetsNotValidException()
        }
    }

    private suspend fun addSetsToExercise(workoutExerciseId: Long, sets: List<Set>) {
        sets.forEach { set ->
            insertSetToExerciseUseCase(workoutExerciseId, set)
        }
    }
}
