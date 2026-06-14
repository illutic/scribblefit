package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Set
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

/**
 * Creates a new scribble from manual exercise input.
 * The scribble is created in SUCCESS status so it appears immediately on the canvas as a parsed entry.
 */
class CreateManualScribbleUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        exerciseName: String,
        muscleGroup: String,
        sets: List<Set>,
        date: CurrentDate = CurrentDate(LocalDateTime.now())
    ): Result<Long> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            val exercise = Exercise(
                id = 0,
                canonicalName = exerciseName,
                muscleGroup = muscleGroup,
                sets = sets,
                createdAt = date.millis
            )

            val scribble = Scribble(
                id = 0,
                rawText = "Manual Entry: $exerciseName",
                status = ScribbleStatus.SUCCESS,
                createdAt = date.millis,
                exercises = listOf(exercise)
            )

            scribbleRepository.insertScribble(scribble)
        }
    }
}
