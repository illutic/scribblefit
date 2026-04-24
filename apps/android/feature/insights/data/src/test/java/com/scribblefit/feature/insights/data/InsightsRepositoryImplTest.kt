package com.scribblefit.feature.insights.data

import app.cash.turbine.test
import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.entity.exercise.ExerciseEntity
import com.scribblefit.core.database.entity.exercise.ExerciseWithSets
import com.scribblefit.core.database.entity.set.SetEntity
import com.scribblefit.feature.ai.domain.LLMEngine
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InsightsRepositoryImplTest {

    private val exerciseDao = mockk<ExerciseDao>()
    private val llmEngine = mockk<LLMEngine>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: InsightsRepositoryImpl

    @Before
    fun setup() {
        repository = InsightsRepositoryImpl(exerciseDao, llmEngine, testDispatcher)
    }

    // region Helpers

    private fun makeExercise(
        exerciseId: Long = 1L,
        scribbleId: Long = 1L,
        name: String = "Bench Press",
        muscleGroup: String = "Chest",
        createdAt: Long = 1_710_000_000_000L,
        sets: List<SetEntity> = emptyList()
    ): ExerciseWithSets {
        return ExerciseWithSets(
            exercise = ExerciseEntity(
                exerciseId = exerciseId,
                scribbleId = scribbleId,
                name = name,
                muscleGroup = muscleGroup,
                createdAt = createdAt
            ),
            sets = sets
        )
    }

    private fun makeSet(
        setId: Long = 1L,
        exerciseId: Long = 1L,
        setNumber: Int = 1,
        weight: Float = 100f,
        reps: Int = 10
    ): SetEntity {
        return SetEntity(
            setId = setId,
            exerciseId = exerciseId,
            setNumber = setNumber,
            weight = weight,
            reps = reps
        )
    }

    // endregion

    // region Volume Tests

    @Test
    fun `getVolumeInsights returns correct volume per session`() = runTest(testDispatcher) {
        // Given: one exercise with bench press: 100kg x 10, 100kg x 8
        val exercise = makeExercise(
            sets = listOf(
                makeSet(setId = 1, weight = 100f, reps = 10),
                makeSet(setId = 2, weight = 100f, reps = 8)
            )
        )
        every { exerciseDao.getExercisesWithSetsInRange(any(), any()) } returns flowOf(
            listOf(exercise)
        )

        // When & Then
        repository.getVolumeInsights(0L, 1L).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            // Volume = (100 * 10) + (100 * 8) = 1800
            assertEquals(1800f, result[0].volume, 0.01f)
            awaitComplete()
        }
    }

    @Test
    fun `getVolumeInsights sums across multiple exercises in one session`() =
        runTest(testDispatcher) {
            // Given: one session (scribbleId 1) with bench (100x10) + row (80x12) at same timestamp
            val createdAt = 1_710_000_000_000L
            val ex1 = makeExercise(
                exerciseId = 1,
                scribbleId = 1,
                name = "Bench Press",
                createdAt = createdAt,
                sets = listOf(makeSet(setId = 1, weight = 100f, reps = 10))
            )
            val ex2 = makeExercise(
                exerciseId = 2,
                scribbleId = 1,
                name = "Barbell Row",
                createdAt = createdAt,
                sets = listOf(makeSet(setId = 2, weight = 80f, reps = 12))
            )
            every { exerciseDao.getExercisesWithSetsInRange(any(), any()) } returns flowOf(
                listOf(ex1, ex2)
            )

            repository.getVolumeInsights(0L, 1L).test {
                val result = awaitItem()
                assertEquals(1, result.size)
                // Volume = (100 * 10) + (80 * 12) = 1000 + 960 = 1960
                assertEquals(1960f, result[0].volume, 0.01f)
                awaitComplete()
            }
        }

    @Test
    fun `getVolumeInsights returns empty for no data`() = runTest(testDispatcher) {
        every {
            exerciseDao.getExercisesWithSetsInRange(any(), any())
        } returns flowOf(emptyList())

        repository.getVolumeInsights(0L, 1L).test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getVolumeInsights returns one point per unique timestamp`() = runTest(testDispatcher) {
        val ex1 = makeExercise(
            exerciseId = 1,
            createdAt = 1_710_000_000_000L,
            sets = listOf(makeSet(weight = 50f, reps = 10))
        )
        val ex2 = makeExercise(
            exerciseId = 2,
            createdAt = 1_710_086_400_000L, // +1 day
            sets = listOf(makeSet(weight = 60f, reps = 10))
        )
        every { exerciseDao.getExercisesWithSetsInRange(any(), any()) } returns flowOf(
            listOf(ex1, ex2)
        )

        repository.getVolumeInsights(0L, 1L).test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals(500f, result[0].volume, 0.01f)
            assertEquals(600f, result[1].volume, 0.01f)
            awaitComplete()
        }
    }

    // endregion

    // region Frequency Tests

    @Test
    fun `getFrequencyInsights returns correct exercise count`() =
        runTest(testDispatcher) {
            val ex1 = makeExercise(exerciseId = 1, name = "Bench Press")
            val ex2 = makeExercise(exerciseId = 2, name = "Incline DB Press")
            val ex3 = makeExercise(exerciseId = 3, name = "Squat")
            
            every { exerciseDao.getExercisesWithSetsInRange(any(), any()) } returns flowOf(
                listOf(ex1, ex2, ex3)
            )

            repository.getFrequencyInsights(0L, 7_000_000_000L).test {
                val result = awaitItem()
                assertEquals(3, result.totalExercises)
                awaitComplete()
            }
        }

    // endregion

    // region Muscle Distribution Tests

    @Test
    fun `getMuscleGroupDistribution returns correct percentages`() = runTest(testDispatcher) {
        // Given: 3 chest exercises, 2 back exercises
        val exercises = listOf(
            makeExercise(exerciseId = 1, muscleGroup = "Chest"),
            makeExercise(exerciseId = 2, muscleGroup = "Chest"),
            makeExercise(exerciseId = 3, muscleGroup = "Chest"),
            makeExercise(exerciseId = 4, muscleGroup = "Back"),
            makeExercise(exerciseId = 5, muscleGroup = "Back")
        )
        every { exerciseDao.getExercisesWithSetsInRange(any(), any()) } returns flowOf(exercises)

        repository.getMuscleDistributionInsights(0L, 1L).test {
            val result = awaitItem()
            assertEquals(2, result.size)

            // Sorted by percentage descending
            val chest = result[0]
            assertEquals("Chest", chest.muscleGroup)
            assertEquals(60f, chest.percentage, 0.01f) // 3/5 * 100

            val back = result[1]
            assertEquals("Back", back.muscleGroup)
            assertEquals(40f, back.percentage, 0.01f) // 2/5 * 100
            awaitComplete()
        }
    }

    // endregion
}
