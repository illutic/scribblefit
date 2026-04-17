package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.ai.domain.LLMEngineProxy
import com.scribblefit.feature.scribble.domain.usecase.GetPendingScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsFailedUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsPendingUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleWithWorkoutUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

class ParsePendingScribblesUseCase(
    private val getPendingScribblesByDateUseCase: GetPendingScribblesByDateUseCase,
    private val updateScribbleWithWorkoutUseCase: UpdateScribbleWithWorkoutUseCase,
    private val updateScribbleAsFailedUseCase: UpdateScribbleAsFailedUseCase,
    private val updateScribbleAsPendingUseCase: UpdateScribbleAsPendingUseCase,
    private val llmEngineProxy: LLMEngineProxy,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    private val logger = LoggerFactory.getLogger("ParsePendingScribblesUseCase")
    private var collectorJob: Job? = null
    private val parsingScribbleIds = ConcurrentHashMap.newKeySet<Long>()

    suspend operator fun invoke(date: LocalDate) = withContext(coroutineDispatcher) {
        collectorJob?.cancel()
        collectorJob = launch {
            getPendingScribblesByDateUseCase(date).collect { pendingScribbles ->
                pendingScribbles
                    .filter { (it.status == ScribbleStatus.PENDING || it.status == ScribbleStatus.PARSING) && !parsingScribbleIds.contains(it.id) }
                    .forEach { scribble ->
                        parseScribble(scribble)
                    }
            }
        }
    }

    suspend fun parseSingleScribble(scribble: Scribble) = withContext(coroutineDispatcher) {
        if (!parsingScribbleIds.contains(scribble.id)) {
            parseScribble(scribble)
        }
    }

    private suspend fun parseScribble(scribble: Scribble) {
        if (!parsingScribbleIds.add(scribble.id)) return
        
        try {
            logger.info("Parsing scribble with id ${scribble.id}")
            updateScribbleAsPendingUseCase(scribble.id)
            val llmEngine = llmEngineProxy.underlyingEngine.first()
            llmEngine.parseWorkout(scribble.rawText)
                .onSuccess {
                    logger.info("Successfully parsed scribble with id ${scribble.id}")
                    updateScribbleWithWorkoutUseCase(
                        id = scribble.id,
                        exercises = it.workout.exercises,
                    )
                }
                .onFailure {
                    logger.error("Failed to parse scribble with id ${scribble.id}", it)
                    updateScribbleAsFailedUseCase(scribble.id)
                }
        } finally {
            parsingScribbleIds.remove(scribble.id)
        }
    }
}
