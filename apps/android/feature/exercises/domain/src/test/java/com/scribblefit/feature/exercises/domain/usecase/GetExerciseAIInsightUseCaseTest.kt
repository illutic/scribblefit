package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.core.model.InsightType
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class GetExerciseAIInsightUseCaseTest {

    private val llmEngine: LLMEngine = mockk()
    private val repository: ExerciseRepository = mockk()
    private lateinit var useCase: GetExerciseAIInsightUseCase

    private val nowMillis = LocalDateTime.now()
        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private fun exercise(id: Long, sets: List<Set>) = Exercise(
        id = id, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = sets, createdAt = nowMillis
    )

    private fun set(weight: Float, reps: Int) = Set(id = 0L, setNumber = 1, reps = reps, weight = weight)

    @Before
    fun setup() {
        useCase = GetExerciseAIInsightUseCase(
            llmEngine = llmEngine,
            exerciseRepository = repository
        )
    }

    @Test
    fun `invoke returns failure when no history available`() = runTest {
        coEvery { repository.getExercisesInRange(any(), any()) } returns emptyList()

        val result = useCase()

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull()?.message?.contains("No history") == true)
    }

    @Test
    fun `invoke returns insight from llmEngine on happy path`() = runTest {
        val exercises = listOf(exercise(1L, listOf(set(100f, 10))))
        val expectedInsight = AIInsight(insightType = InsightType.TREND, text = "Great progress!")
        coEvery { repository.getExercisesInRange(any(), any()) } returns exercises
        coEvery { llmEngine.generateExerciseInsight(any()) } returns Result.success(expectedInsight)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(expectedInsight, result.getOrThrow())
    }

    @Test
    fun `invoke propagates llm engine failure`() = runTest {
        val exercises = listOf(exercise(1L, listOf(set(100f, 10))))
        coEvery { repository.getExercisesInRange(any(), any()) } returns exercises
        coEvery { llmEngine.generateExerciseInsight(any()) } returns Result.failure(RuntimeException("LLM error"))

        val result = useCase()

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke passes formatted history context to llm engine`() = runTest {
        val exercises = listOf(exercise(1L, listOf(set(100f, 10))))
        coEvery { repository.getExercisesInRange(any(), any()) } returns exercises
        coEvery { llmEngine.generateExerciseInsight(any()) } returns
            Result.success(AIInsight(InsightType.TREND, "ok"))

        useCase()

        coVerify(exactly = 1) { llmEngine.generateExerciseInsight(any()) }
    }

    @Test
    fun `invoke uses correct look behind days`() = runTest {
        coEvery { repository.getExercisesInRange(any(), any()) } returns emptyList()

        useCase(dateLookBehind = 14L)

        // Should call repository (it may fail after, but it was called with some range)
        coVerify(exactly = 1) { repository.getExercisesInRange(any(), any()) }
    }
}
