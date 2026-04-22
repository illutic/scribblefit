package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleError
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class UpdateScribbleWithWorkoutUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Long, exercises: List<Exercise>) =
        runCatchingWithCancellation {
            withContext(coroutineDispatcher) {
                val scribble = scribbleRepository.getScribble(id).firstOrNull()
                    ?: throw ScribbleError.NotFound(id)
                
                scribbleRepository.updateScribbleExercises(id, exercises)
                
                scribbleRepository.updateScribble(
                    scribble.copy(
                        status = ScribbleStatus.SUCCESS,
                        parsedJson = null
                    )
                )
            }
        }
}
