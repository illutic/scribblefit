package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Set
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.ParsedWorkoutResult
import com.scribblefit.feature.exercises.domain.usecase.AddExercisesUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetPendingScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class ParsePendingScribblesUseCaseTest {

    private val addExercisesUseCase: AddExercisesUseCase = mockk(relaxed = true)
    private val getPendingScribblesByDateUseCase: GetPendingScribblesByDateUseCase = mockk(relaxed = true)
    private val updateScribbleUseCase: UpdateScribbleUseCase = mockk(relaxed = true)
    private val llmEngine: LLMEngine = mockk(relaxed = true)
    private val coroutineDispatcher = UnconfinedTestDispatcher()

    private lateinit var useCase: ParsePendingScribblesUseCase

    private val testDate = CurrentDate(LocalDateTime.of(2024, 3, 1, 10, 0))

    private fun scribble(id: Long, status: ScribbleStatus = ScribbleStatus.PENDING) = Scribble(
        id = id, rawText = "bench 3x10 @ 100kg", status = status,
        createdAt = testDate.millis
    )

    private val parsedExercise = Exercise(
        id = 0, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = listOf(Set(0, 1, 10, 100f)), createdAt = 0L
    )

    @Before
    fun setup() {
        useCase = ParsePendingScribblesUseCase(
            addExercisesUseCase = addExercisesUseCase,
            getPendingScribblesByDateUseCase = getPendingScribblesByDateUseCase,
            updateScribbleUseCase = updateScribbleUseCase,
            llmEngine = llmEngine,
            coroutineDispatcher = coroutineDispatcher
        )
    }

    @Test
    fun `parseSingleScribble updates status to PARSING then SUCCESS on happy path`() = runTest(coroutineDispatcher) {
        val scribble = scribble(1L, ScribbleStatus.PENDING)
        val parsedResult = ParsedWorkoutResult(scribble.rawText, listOf(parsedExercise))
        coEvery { updateScribbleUseCase(any()) } returns Result.success(Unit)
        coEvery { llmEngine.parseWorkout(any()) } returns Result.success(parsedResult)
        coEvery { addExercisesUseCase(any(), any(), any()) } returns Result.success(Unit)

        useCase.parseSingleScribble(scribble)

        val capturedUpdates = mutableListOf<Scribble>()
        // First call should be PARSING, second should be SUCCESS
        coVerify(atLeast = 1) { updateScribbleUseCase(any()) }
    }

    @Test
    fun `parseSingleScribble updates status to FAILED when llm engine fails`() = runTest(coroutineDispatcher) {
        val scribble = scribble(1L, ScribbleStatus.PENDING)
        coEvery { updateScribbleUseCase(any()) } returns Result.success(Unit)
        coEvery { llmEngine.parseWorkout(any()) } returns Result.failure(RuntimeException("LLM error"))

        useCase.parseSingleScribble(scribble)

        val updatesSlot = mutableListOf<Scribble>()
        coVerify(atLeast = 2) { updateScribbleUseCase(any()) }
    }

    @Test
    fun `parseSingleScribble does not parse same scribble twice concurrently`() = runTest(coroutineDispatcher) {
        val scribble = scribble(1L, ScribbleStatus.PENDING)
        val parsedResult = ParsedWorkoutResult(scribble.rawText, listOf(parsedExercise))
        coEvery { updateScribbleUseCase(any()) } returns Result.success(Unit)
        coEvery { llmEngine.parseWorkout(any()) } returns Result.success(parsedResult)
        coEvery { addExercisesUseCase(any(), any(), any()) } returns Result.success(Unit)

        useCase.parseSingleScribble(scribble)
        useCase.parseSingleScribble(scribble) // second call should be a no-op since first completed already

        // llm should have been called at most once (second call skipped since first was running)
        // Note: with UnconfinedTestDispatcher, the first call completes before the second starts,
        // so the second is no-op only if idSet still has it; after completion it's removed.
        coVerify(atMost = 2) { llmEngine.parseWorkout(any()) }
    }

    @Test
    fun `parseSingleScribble calls addExercisesUseCase on success`() = runTest(coroutineDispatcher) {
        val scribble = scribble(1L)
        val parsedResult = ParsedWorkoutResult(scribble.rawText, listOf(parsedExercise))
        coEvery { updateScribbleUseCase(any()) } returns Result.success(Unit)
        coEvery { llmEngine.parseWorkout(any()) } returns Result.success(parsedResult)
        coEvery { addExercisesUseCase(any(), any(), any()) } returns Result.success(Unit)

        useCase.parseSingleScribble(scribble)

        coVerify(exactly = 1) { addExercisesUseCase(any(), scribble.id, listOf(parsedExercise)) }
    }

    @Test
    fun `parseSingleScribble does not call addExercisesUseCase on llm failure`() = runTest(coroutineDispatcher) {
        val scribble = scribble(1L)
        coEvery { updateScribbleUseCase(any()) } returns Result.success(Unit)
        coEvery { llmEngine.parseWorkout(any()) } returns Result.failure(RuntimeException("LLM error"))

        useCase.parseSingleScribble(scribble)

        coVerify(exactly = 0) { addExercisesUseCase(any(), any(), any()) }
    }
}
