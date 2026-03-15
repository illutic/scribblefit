package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Workout
import com.scribblefit.feature.scribble.domain.ScribbleNotFoundException
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.workouts.domain.usecase.GetWorkoutUseCase
import com.scribblefit.feature.workouts.domain.usecase.InsertWorkoutUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class UpdateScribbleWithWorkoutUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val insertWorkoutUseCase: InsertWorkoutUseCase,
    private val getWorkoutUseCase: GetWorkoutUseCase,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Long, workout: Workout) =
        runCatchingWithCancellation {
            withContext(coroutineDispatcher) {
                val workoutId = insertWorkoutUseCase(workout).getOrThrow()
                insertExercisesToScribble(id, workoutId)
                updateScribbleStatusToParsed(id)
            }
        }

    private suspend fun insertExercisesToScribble(scribbleId: Long, workoutId: Long) {
        val workout = getWorkoutUseCase(workoutId).getOrThrow()
        val exerciseIds = workout?.exercises?.map { exercise -> exercise.id } ?: emptyList()
        exerciseIds.forEach { exerciseId ->
            scribbleRepository.addExerciseToScribble(scribbleId, exerciseId)
        }
    }

    private suspend fun updateScribbleStatusToParsed(scribbleId: Long) {
        val scribble = scribbleRepository.getScribble(scribbleId).firstOrNull()
            ?: throw ScribbleNotFoundException(scribbleId)
        scribbleRepository.updateScribble(
            scribble.copy(
                status = ScribbleStatus.PARSED
            )
        )
    }
}
