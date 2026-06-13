package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.core.model.TrendDirection
import com.scribblefit.core.model.WeeklyStats
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateTrendsUseCaseTest {

    private val repository = mockk<ExerciseRepository>()
    private val weeklyStatsUseCase = mockk<CalculateWeeklyStatsUseCase>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = CalculateTrendsUseCase(repository, weeklyStatsUseCase, testDispatcher)

    private fun exercise(
        id: Long,
        name: String = "Bench Press",
        sets: List<Set> = emptyList(),
        createdAt: Long = id * 1000
    ) = Exercise(
        id = id,
        canonicalName = name,
        muscleGroup = "Chest",
        sets = sets,
        createdAt = createdAt
    )

    private fun set(weight: Float, reps: Int) = Set(
        id = 0L,
        setNumber = 1,
        reps = reps,
        weight = weight
    )

    private val defaultWeeklyStats =
        WeeklyStats(sessions = 2, totalVolume = 1000f, maxWeight = 100f)

    @Test
    fun `should calculate lastVolume as sum of weight times reps`() = runTest(testDispatcher) {
        val current = exercise(2, sets = listOf(set(100f, 10), set(80f, 8)))
        coEvery { repository.getExerciseById(2) } returns current
        coEvery { repository.getExercisesByName("Bench Press") } returns listOf(current)
        coEvery { weeklyStatsUseCase(2) } returns Result.success(defaultWeeklyStats)

        val result = useCase(2)

        assertTrue(result.isSuccess)
        // 100*10 + 80*8 = 1000 + 640 = 1640
        assertEquals(1640f, result.getOrThrow().lastVolume, 0.01f)
    }

    @Test
    fun `should return IMPROVING when lastVolume exceeds previousVolume`() =
        runTest(testDispatcher) {
            val previous = exercise(1, sets = listOf(set(80f, 8))) // volume = 640
            val current = exercise(2, sets = listOf(set(100f, 10))) // volume = 1000
            coEvery { repository.getExerciseById(2) } returns current
            coEvery { repository.getExercisesByName("Bench Press") } returns listOf(
                previous,
                current
            )
            coEvery { weeklyStatsUseCase(2) } returns Result.success(defaultWeeklyStats)

            val result = useCase(2)

            assertEquals(TrendDirection.IMPROVING, result.getOrThrow().lastVolumeTrend)
        }

    @Test
    fun `should return DECLINING when lastVolume is below previousVolume`() =
        runTest(testDispatcher) {
            val previous = exercise(1, sets = listOf(set(100f, 10))) // volume = 1000
            val current = exercise(2, sets = listOf(set(80f, 8))) // volume = 640
            coEvery { repository.getExerciseById(2) } returns current
            coEvery { repository.getExercisesByName("Bench Press") } returns listOf(
                previous,
                current
            )
            coEvery { weeklyStatsUseCase(2) } returns Result.success(defaultWeeklyStats)

            val result = useCase(2)

            assertEquals(TrendDirection.DECLINING, result.getOrThrow().lastVolumeTrend)
        }

    @Test
    fun `should return STABLE when lastVolume equals previousVolume`() = runTest(testDispatcher) {
        val previous = exercise(1, sets = listOf(set(100f, 10))) // volume = 1000
        val current = exercise(2, sets = listOf(set(100f, 10))) // volume = 1000
        coEvery { repository.getExerciseById(2) } returns current
        coEvery { repository.getExercisesByName("Bench Press") } returns listOf(previous, current)
        coEvery { weeklyStatsUseCase(2) } returns Result.success(defaultWeeklyStats)

        val result = useCase(2)

        assertEquals(TrendDirection.STABLE, result.getOrThrow().lastVolumeTrend)
    }

    @Test
    fun `should return STABLE volume trend when no previous session exists`() =
        runTest(testDispatcher) {
            val current = exercise(1, sets = listOf(set(100f, 10)))
            coEvery { repository.getExerciseById(1) } returns current
            coEvery { repository.getExercisesByName("Bench Press") } returns listOf(current)
            coEvery { weeklyStatsUseCase(1) } returns Result.success(defaultWeeklyStats)

            val result = useCase(1)

            assertTrue(result.isSuccess)
            assertEquals(1000f, result.getOrThrow().lastVolume, 0.01f)
            assertEquals(TrendDirection.STABLE, result.getOrThrow().lastVolumeTrend)
        }
}
