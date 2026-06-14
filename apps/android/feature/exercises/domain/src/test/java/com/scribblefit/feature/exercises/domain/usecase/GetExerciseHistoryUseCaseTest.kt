package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.ExerciseHistorySession
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetExerciseHistoryUseCaseTest {

    private val repository: ExerciseRepository = mockk()
    private val formatUseCase: FormatExerciseSummaryUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetExerciseHistoryUseCase

    private fun exercise(id: Long, sets: List<Set>, createdAt: Long = id * 1000L) = Exercise(
        id = id, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = sets, createdAt = createdAt
    )

    private fun set(weight: Float, reps: Int) = Set(id = 0L, setNumber = 1, reps = reps, weight = weight)

    @Before
    fun setup() {
        useCase = GetExerciseHistoryUseCase(
            exerciseRepository = repository,
            formatExerciseSummaryUseCase = formatUseCase,
            coroutineDispatcher = testDispatcher
        )
        io.mockk.every { formatUseCase(any(), any()) } returns "100.0kg • 3 sets x 10 reps"
    }

    @Test
    fun `invoke returns empty list when no history found`() = runTest(testDispatcher) {
        coEvery { repository.getExercisesByName(any()) } returns emptyList()

        val result = useCase("Bench Press", Weight.KGS)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `invoke returns sorted sessions by date descending`() = runTest(testDispatcher) {
        val exercises = listOf(
            exercise(1L, listOf(set(100f, 10)), createdAt = 1000L),
            exercise(2L, listOf(set(100f, 10)), createdAt = 3000L),
            exercise(3L, listOf(set(100f, 10)), createdAt = 2000L)
        )
        coEvery { repository.getExercisesByName("Bench Press") } returns exercises

        val result = useCase("Bench Press", Weight.KGS)

        assertTrue(result.isSuccess)
        val sessions = result.getOrThrow()
        assertEquals(3, sessions.size)
        // Should be sorted descending: 3000, 2000, 1000
        assertTrue(sessions[0].date >= sessions[1].date)
        assertTrue(sessions[1].date >= sessions[2].date)
    }

    @Test
    fun `invoke calculates totalVolume per session correctly`() = runTest(testDispatcher) {
        val exercises = listOf(
            exercise(1L, listOf(set(100f, 10), set(80f, 8)))
        )
        coEvery { repository.getExercisesByName("Bench Press") } returns exercises

        val result = useCase("Bench Press", Weight.KGS)

        assertTrue(result.isSuccess)
        // 100*10 + 80*8 = 1640
        assertEquals(1640f, result.getOrThrow()[0].totalVolume, 0.01f)
    }

    @Test
    fun `invoke identifies personal best by max weight`() = runTest(testDispatcher) {
        val lowExercise = exercise(1L, listOf(set(80f, 10)), createdAt = 1000L)
        val highExercise = exercise(2L, listOf(set(120f, 5)), createdAt = 2000L)
        coEvery { repository.getExercisesByName("Bench Press") } returns listOf(lowExercise, highExercise)

        val result = useCase("Bench Press", Weight.KGS)

        assertTrue(result.isSuccess)
        val sessions = result.getOrThrow()
        val pbSession = sessions.first { it.exercise.id == 2L }
        assertTrue(pbSession.isPersonalBest)
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        coEvery { repository.getExercisesByName(any()) } throws RuntimeException("DB error")

        val result = useCase("Bench Press", Weight.KGS)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke passes exercise and weightUnit to format use case`() = runTest(testDispatcher) {
        val exercises = listOf(exercise(1L, listOf(set(100f, 10))))
        coEvery { repository.getExercisesByName("Bench Press") } returns exercises

        useCase("Bench Press", Weight.LBS)

        io.mockk.verify(exactly = 1) { formatUseCase(exercises[0], Weight.LBS) }
    }
}
