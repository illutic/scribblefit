package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Manually adds an exercise with sets to a workout.
 */
class AddExerciseUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        date: CurrentDate,
        scribbleId: Long,
        exerciseName: String,
        muscleGroup: String,
        sets: List<Set>
    ): Result<Unit> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            val exercise = Exercise(
                id = 0,
                canonicalName = exerciseName,
                muscleGroup = muscleGroup,
                sets = sets,
                createdAt = date.millis
            )
            exerciseRepository.addExercisesWithSets(scribbleId, listOf(exercise))
        }
    }
}
