package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.exercises.domain.usecase.AddExercisesUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetPendingScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.time.ZoneId
import kotlin.time.Instant
import kotlin.time.toJavaInstant

class ParsePendingScribblesUseCase(
    private val addExercisesUseCase: AddExercisesUseCase,
    private val getPendingScribblesByDateUseCase: GetPendingScribblesByDateUseCase,
    private val updateScribbleUseCase: UpdateScribbleUseCase,
    private val llmEngine: LLMEngine,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    private val logger = LoggerFactory.getLogger("ParsePendingScribblesUseCase")
    private val parsingScribbleIds = hashSetOf<Long>()

    suspend operator fun invoke(date: CurrentDate) = withContext(coroutineDispatcher) {
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
            updateScribbleUseCase(scribble.copy(status = ScribbleStatus.PARSING))
            val localDate = Instant.fromEpochMilliseconds(scribble.createdAt)
                .toJavaInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

            llmEngine.parseWorkout(scribble.rawText)
                .onSuccess {
                    logger.info("Successfully parsed scribble with id ${scribble.id}")
                    updateScribbleUseCase(scribble.copy(status = ScribbleStatus.SUCCESS))
                    addExercisesUseCase(
                        date = CurrentDate(localDate),
                        scribbleId = scribble.id,
                        exercises = it.exercises
                    )
                }
                .onFailure {
                    logger.error("Failed to parse scribble with id ${scribble.id}", it)
                    updateScribbleUseCase(scribble.copy(status = ScribbleStatus.FAILED))
                }
        } catch (e: Exception) {
            logger.error("Unexpected error parsing scribble ${scribble.id}", e)
            updateScribbleUseCase(scribble.copy(status = ScribbleStatus.FAILED))
        } finally {
            parsingScribbleIds.remove(scribble.id)
        }
    }
}
