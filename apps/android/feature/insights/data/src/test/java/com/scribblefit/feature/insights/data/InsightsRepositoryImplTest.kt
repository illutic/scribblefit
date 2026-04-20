package com.scribblefit.feature.insights.data

import app.cash.turbine.test
import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.core.database.entity.exercise.Exercise
import com.scribblefit.core.database.entity.exercise.WorkoutExercise
import com.scribblefit.core.database.entity.exercise.WorkoutExerciseWithDetails
import com.scribblefit.core.database.entity.set.WorkoutSet
import com.scribblefit.core.database.entity.workout.Workout
import com.scribblefit.core.database.entity.workout.WorkoutWithAllDetails
import com.scribblefit.feature.ai.domain.LLMEngine
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InsightsRepositoryImplTest {

    private val workoutDao = mockk<WorkoutDao>()
    private val llmEngine = mockk<LLMEngine>()
    private val testDispatcher = StandardTestDispatcher()
    private val dispatcherProvider = object : CoroutineDispatcherProvider {
        override fun main(): CoroutineDispatcher = testDispatcher
        override fun default(): CoroutineDispatcher = testDispatcher
        override fun io(): CoroutineDispatcher = testDispatcher
    }
    private lateinit var repository: InsightsRepositoryImpl

    @Before
    fun setup() {
        repository = InsightsRepositoryImpl(workoutDao, llmEngine, dispatcherProvider)
    }

    // region Helpers

    private fun makeWorkout(
        workoutId: Long = 1L,
        date: Long = 1_710_000_000_000L, // ~2024-03-09
        exercises: List<WorkoutExerciseWithDetails> = emptyList()
    ): WorkoutWithAllDetails {
        return WorkoutWithAllDetails(
            workout = Workout(workoutId = workoutId, workoutDate = date),
            exercises = exercises
        )
    }

    private fun makeExercise(
        workoutExerciseId: Long = 1L,
        workoutId: Long = 1L,
        exerciseId: Long = 1L,
        name: String = "Bench Press",
        muscleGroup: String = "Chest",
        sets: List<WorkoutSet> = emptyList()
    ): WorkoutExerciseWithDetails {
        return WorkoutExerciseWithDetails(
            workoutExercise = WorkoutExercise(
                workoutExerciseId = workoutExerciseId,
                workoutId = workoutId,
                exerciseId = exerciseId
            ),
            exercise = Exercise(
                exerciseId = exerciseId,
                name = name,
                muscleGroup = muscleGroup
            ),
            sets = sets
        )
    }

    private fun makeSet(
        setId: Long = 1L,
        workoutExerciseId: Long = 1L,
        setNumber: Int = 1,
        weight: Float = 100f,
        reps: Int = 10
    ): WorkoutSet {
        return WorkoutSet(
            setId = setId,
            workoutExerciseId = workoutExerciseId,
            setNumber = setNumber,
            weight = weight,
            reps = reps
        )
    }

    // endregion

    // region Volume Tests

    @Test
    fun `getVolumeInsights returns correct volume per workout`() = runTest(testDispatcher) {
        // Given: one workout with bench press: 100kg x 10, 100kg x 8
        val workout = makeWorkout(
            exercises = listOf(
                makeExercise(
                    sets = listOf(
                        makeSet(setId = 1, weight = 100f, reps = 10),
                        makeSet(setId = 2, weight = 100f, reps = 8)
                    )
                )
            )
        )
        every { workoutDao.getWorkoutsWithAllDetailsInRange(any(), any()) } returns flowOf(listOf(workout))

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
    fun `getVolumeInsights sums across multiple exercises in one workout`() = runTest(testDispatcher) {
        // Given: one workout with bench (100x10) + row (80x12)
        val workout = makeWorkout(
            exercises = listOf(
                makeExercise(
                    workoutExerciseId = 1,
                    exerciseId = 1,
                    name = "Bench Press",
                    muscleGroup = "Chest",
                    sets = listOf(makeSet(setId = 1, weight = 100f, reps = 10))
                ),
                makeExercise(
                    workoutExerciseId = 2,
                    exerciseId = 2,
                    name = "Barbell Row",
                    muscleGroup = "Back",
                    sets = listOf(makeSet(setId = 2, weight = 80f, reps = 12))
                )
            )
        )
        every { workoutDao.getWorkoutsWithAllDetailsInRange(any(), any()) } returns flowOf(listOf(workout))

        repository.getVolumeInsights(0L, 1L).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            // Volume = (100 * 10) + (80 * 12) = 1000 + 960 = 1960
            assertEquals(1960f, result[0].volume, 0.01f)
            awaitComplete()
        }
    }

    @Test
    fun `getVolumeInsights returns empty for no workouts`() = runTest(testDispatcher) {
        every { workoutDao.getWorkoutsWithAllDetailsInRange(any(), any()) } returns flowOf(emptyList())

        repository.getVolumeInsights(0L, 1L).test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getVolumeInsights returns one point per workout`() = runTest(testDispatcher) {
        val workout1 = makeWorkout(
            workoutId = 1,
            date = 1_710_000_000_000L,
            exercises = listOf(
                makeExercise(sets = listOf(makeSet(weight = 50f, reps = 10)))
            )
        )
        val workout2 = makeWorkout(
            workoutId = 2,
            date = 1_710_086_400_000L, // +1 day
            exercises = listOf(
                makeExercise(sets = listOf(makeSet(weight = 60f, reps = 10)))
            )
        )
        every { workoutDao.getWorkoutsWithAllDetailsInRange(any(), any()) } returns flowOf(listOf(workout1, workout2))

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
    fun `getFrequencyInsights returns correct workout count and exercise count`() = runTest(testDispatcher) {
        val workout1 = makeWorkout(
            workoutId = 1,
            date = 1_710_000_000_000L,
            exercises = listOf(
                makeExercise(workoutExerciseId = 1, exerciseId = 1, name = "Bench Press"),
                makeExercise(workoutExerciseId = 2, exerciseId = 2, name = "Incline DB Press")
            )
        )
        val workout2 = makeWorkout(
            workoutId = 2,
            date = 1_710_604_800_000L, // +7 days
            exercises = listOf(
                makeExercise(workoutExerciseId = 3, exerciseId = 3, name = "Squat"),
                makeExercise(workoutExerciseId = 4, exerciseId = 4, name = "Leg Press"),
                makeExercise(workoutExerciseId = 5, exerciseId = 5, name = "Leg Curl")
            )
        )
        every { workoutDao.getWorkoutsWithAllDetailsInRange(any(), any()) } returns flowOf(listOf(workout1, workout2))

        repository.getFrequencyInsights(0L, 1L).test {
            val result = awaitItem()
            assertEquals(2, result.totalWorkouts)
            assertEquals(5, result.totalExercises) // 2 + 3
            awaitComplete()
        }
    }

    @Test
    fun `getFrequencyInsights returns zero for no workouts`() = runTest(testDispatcher) {
        every { workoutDao.getWorkoutsWithAllDetailsInRange(any(), any()) } returns flowOf(emptyList())

        repository.getFrequencyInsights(0L, 1L).test {
            val result = awaitItem()
            assertEquals(0, result.totalWorkouts)
            assertEquals(0, result.totalExercises)
            assertEquals(0f, result.workoutsPerWeek, 0.01f)
            awaitComplete()
        }
    }

    // endregion

    // region Muscle Distribution Tests

    @Test
    fun `getMuscleDistributionInsights returns correct percentages`() = runTest(testDispatcher) {
        // Given: 3 chest exercises, 2 back exercises
        val workout = makeWorkout(
            exercises = listOf(
                makeExercise(workoutExerciseId = 1, exerciseId = 1, muscleGroup = "Chest"),
                makeExercise(workoutExerciseId = 2, exerciseId = 2, muscleGroup = "Chest"),
                makeExercise(workoutExerciseId = 3, exerciseId = 3, muscleGroup = "Chest"),
                makeExercise(workoutExerciseId = 4, exerciseId = 4, muscleGroup = "Back"),
                makeExercise(workoutExerciseId = 5, exerciseId = 5, muscleGroup = "Back")
            )
        )
        every { workoutDao.getWorkoutsWithAllDetailsInRange(any(), any()) } returns flowOf(listOf(workout))

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

    @Test
    fun `getMuscleDistributionInsights returns 100 percent for single muscle group`() = runTest(testDispatcher) {
        val workout = makeWorkout(
            exercises = listOf(
                makeExercise(workoutExerciseId = 1, exerciseId = 1, muscleGroup = "Chest"),
                makeExercise(workoutExerciseId = 2, exerciseId = 2, muscleGroup = "Chest")
            )
        )
        every { workoutDao.getWorkoutsWithAllDetailsInRange(any(), any()) } returns flowOf(listOf(workout))

        repository.getMuscleDistributionInsights(0L, 1L).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Chest", result[0].muscleGroup)
            assertEquals(100f, result[0].percentage, 0.01f)
            awaitComplete()
        }
    }

    @Test
    fun `getMuscleDistributionInsights returns empty for no exercises`() = runTest(testDispatcher) {
        val workout = makeWorkout(exercises = emptyList())
        every { workoutDao.getWorkoutsWithAllDetailsInRange(any(), any()) } returns flowOf(listOf(workout))

        repository.getMuscleDistributionInsights(0L, 1L).test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getMuscleDistributionInsights aggregates across multiple workouts`() = runTest(testDispatcher) {
        val workout1 = makeWorkout(
            workoutId = 1,
            exercises = listOf(
                makeExercise(workoutExerciseId = 1, exerciseId = 1, muscleGroup = "Chest")
            )
        )
        val workout2 = makeWorkout(
            workoutId = 2,
            exercises = listOf(
                makeExercise(workoutExerciseId = 2, exerciseId = 2, muscleGroup = "Chest"),
                makeExercise(workoutExerciseId = 3, exerciseId = 3, muscleGroup = "Back"),
                makeExercise(workoutExerciseId = 4, exerciseId = 4, muscleGroup = "Shoulders")
            )
        )
        every { workoutDao.getWorkoutsWithAllDetailsInRange(any(), any()) } returns flowOf(listOf(workout1, workout2))

        repository.getMuscleDistributionInsights(0L, 1L).test {
            val result = awaitItem()
            assertEquals(3, result.size)

            // Chest: 2/4 = 50%, Back: 1/4 = 25%, Shoulders: 1/4 = 25%
            assertEquals("Chest", result[0].muscleGroup)
            assertEquals(50f, result[0].percentage, 0.01f)
            // Back and Shoulders both 25%, order may vary but both present
            val remaining = result.drop(1).map { it.muscleGroup to it.percentage }
            assertTrue(remaining.all { it.second == 25f })
            awaitComplete()
        }
    }

    // endregion
}
