package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.core.model.WeeklyStats
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
import java.time.LocalDateTime
import java.time.ZoneId

class CalculateWeeklyStatsUseCaseTest {

    private val repository: ExerciseRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: CalculateWeeklyStatsUseCase

    private fun makeExercise(
        id: Long,
        name: String = "Bench Press",
        sets: List<Set> = emptyList(),
        createdAt: Long = System.currentTimeMillis()
    ) = Exercise(
        id = id,
        canonicalName = name,
        muscleGroup = "Chest",
        sets = sets,
        createdAt = createdAt
    )

    private fun set(weight: Float, reps: Int) = Set(id = 0, setNumber = 1, reps = reps, weight = weight)

    private val fixedDate: Long = LocalDateTime.of(2024, 6, 1, 12, 0)
        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    @Before
    fun setup() {
        useCase = CalculateWeeklyStatsUseCase(
            exerciseRepository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns failure when exercise is not found`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(999L) } returns null

        val result = useCase(999L)

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `invoke calculates totalVolume correctly`() = runTest(testDispatcher) {
        val exercise = makeExercise(1L, createdAt = fixedDate, sets = listOf(set(100f, 10), set(80f, 8)))
        coEvery { repository.getExerciseById(1L) } returns exercise
        coEvery { repository.getExercisesInRange(any(), any()) } returns listOf(exercise)

        val result = useCase(1L)

        assertTrue(result.isSuccess)
        // 100*10 + 80*8 = 1000 + 640 = 1640
        assertEquals(1640f, result.getOrThrow().totalVolume, 0.01f)
    }

    @Test
    fun `invoke calculates maxWeight correctly`() = runTest(testDispatcher) {
        val exercise = makeExercise(1L, createdAt = fixedDate, sets = listOf(set(100f, 10), set(120f, 3), set(80f, 12)))
        coEvery { repository.getExerciseById(1L) } returns exercise
        coEvery { repository.getExercisesInRange(any(), any()) } returns listOf(exercise)

        val result = useCase(1L)

        assertTrue(result.isSuccess)
        assertEquals(120f, result.getOrThrow().maxWeight, 0.01f)
    }

    @Test
    fun `invoke calculates sessions correctly across multiple exercises`() = runTest(testDispatcher) {
        val exercise = makeExercise(1L, createdAt = fixedDate)
        val exercise2 = makeExercise(2L, createdAt = fixedDate - 86_400_000L) // 1 day earlier
        coEvery { repository.getExerciseById(1L) } returns exercise
        coEvery { repository.getExercisesInRange(any(), any()) } returns listOf(exercise, exercise2)

        val result = useCase(1L)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().sessions)
    }

    @Test
    fun `invoke filters exercises by canonicalName`() = runTest(testDispatcher) {
        val benchExercise = makeExercise(1L, name = "Bench Press", createdAt = fixedDate, sets = listOf(set(100f, 10)))
        val squatExercise = makeExercise(2L, name = "Squat", createdAt = fixedDate, sets = listOf(set(150f, 5)))
        coEvery { repository.getExerciseById(1L) } returns benchExercise
        coEvery { repository.getExercisesInRange(any(), any()) } returns listOf(benchExercise, squatExercise)

        val result = useCase(1L)

        assertTrue(result.isSuccess)
        // Should only count bench press: 100*10 = 1000
        assertEquals(1000f, result.getOrThrow().totalVolume, 0.01f)
        assertEquals(1, result.getOrThrow().sessions)
    }

    @Test
    fun `invoke returns failure when no exercises found in range`() = runTest(testDispatcher) {
        val exercise = makeExercise(1L, createdAt = fixedDate)
        coEvery { repository.getExerciseById(1L) } returns exercise
        coEvery { repository.getExercisesInRange(any(), any()) } returns emptyList()

        val result = useCase(1L)

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
}
