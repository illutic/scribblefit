package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleNotFoundException
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
                    ?: throw ScribbleNotFoundException(id)
                
                scribbleRepository.clearScribbleExercises(id)
                scribbleRepository.saveScribbleExercises(id, exercises)
                
                scribbleRepository.updateScribble(
                    scribble.copy(
                        status = ScribbleStatus.SUCCESS,
                        parsedJson = null // We now store structured data instead of raw JSON
                    )
                )
            }
        }
}
