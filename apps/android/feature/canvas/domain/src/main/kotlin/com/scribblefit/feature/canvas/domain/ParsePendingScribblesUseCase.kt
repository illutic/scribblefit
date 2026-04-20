package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.scribble.domain.usecase.GetPendingScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsFailedUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsPendingUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleWithWorkoutUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
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
    private val llmEngine: LLMEngine,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    private val logger = LoggerFactory.getLogger("ParsePendingScribblesUseCase")
    private var collectorJob: Job? = null
    private val parsingScribbleIds = ConcurrentHashMap.newKeySet<Long>()

    suspend operator fun invoke(date: LocalDate) = withContext(coroutineDispatcher) {
        collectorJob?.cancel()
        // We DON'T clear parsingScribbleIds here because a previous background parse might still be running
        // and we don't want to double-parse.
        
        collectorJob = launch {
            getPendingScribblesByDateUseCase(date).collect { pendingScribbles ->
                pendingScribbles
                    .filter { scribble ->
                        val isPending = scribble.status == ScribbleStatus.PENDING
                                || scribble.status == ScribbleStatus.PARSING
                        isPending && !parsingScribbleIds.contains(scribble.id)
                    }
                    .forEach { scribble ->
                        launch {
                            parseScribble(scribble)
                        }
                    }
            }
        }
    }

    suspend fun parseSingleScribble(scribble: Scribble) = withContext(coroutineDispatcher) {
        if (!parsingScribbleIds.contains(scribble.id)) {
            launch {
                parseScribble(scribble)
            }
        }
    }

    private suspend fun parseScribble(scribble: Scribble) {
        if (!parsingScribbleIds.add(scribble.id)) return
        
        try {
            logger.info("Parsing scribble with id ${scribble.id}")
            // Update status to PARSING in DB to show progress indicator
            updateScribbleAsPendingUseCase(scribble.id) 
            // Note: updateScribbleAsPendingUseCase actually sets it to PARSING state in the implementation
            // despite the name (legacy naming from initial implementation).
            
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
        } catch (e: Exception) {
            logger.error("Unexpected error parsing scribble ${scribble.id}", e)
            updateScribbleAsFailedUseCase(scribble.id)
        } finally {
            parsingScribbleIds.remove(scribble.id)
        }
    }
}
