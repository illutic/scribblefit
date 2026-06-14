package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Manually adds multiple exercises with sets to a workout in a batch operation.
 */
class AddExercisesUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        date: CurrentDate,
        scribbleId: Long,
        exercises: List<Exercise>
    ): Result<Unit> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            val exercisesWithDate = exercises.map {
                it.copy(createdAt = date.millis)
            }
            exerciseRepository.addExercisesWithSets(scribbleId, exercisesWithDate)
        }
    }
}
