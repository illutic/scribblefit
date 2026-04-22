package com.scribblefit.feature.exercises.ui

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Workout
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.workouts.domain.usecase.GetWorkoutWithExercisesUseCase
import com.scribblefit.feature.workouts.domain.usecase.CalculateWorkoutVolumeUseCase
import com.scribblefit.feature.workouts.domain.usecase.FormatWorkoutSummaryUseCase
import com.scribblefit.feature.exercises.domain.usecase.FormatExerciseSummaryUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutExercisesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getWorkoutWithExercisesUseCase: GetWorkoutWithExercisesUseCase = mockk()
    private val calculateWorkoutVolumeUseCase: CalculateWorkoutVolumeUseCase = mockk()
    private val formatExerciseSummaryUseCase: FormatExerciseSummaryUseCase = mockk()
    private val formatWorkoutSummaryUseCase: FormatWorkoutSummaryUseCase = mockk()
    private val configRepository: ConfigRepository = mockk()
    private val navigator: Navigator = mockk(relaxed = true)
    
    private lateinit var viewModel: WorkoutExercisesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { configRepository.config } returns MutableStateFlow(SystemConfig())
        every { calculateWorkoutVolumeUseCase(any()) } returns 100.0
        every { formatWorkoutSummaryUseCase(any()) } returns FormatWorkoutSummaryUseCase.VolumeSummary("100", false)
        every { formatExerciseSummaryUseCase(any(), any()) } returns "Summary"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadWorkout updates state with workout exercises`() = runTest {
        // Given
        val workoutId = 1L
        val workout = Workout(
            id = workoutId,
            date = System.currentTimeMillis(),
            exercises = listOf(
                Exercise(id = 1, canonicalName = "Pushups", muscleGroup = "Chest", sets = emptyList())
            )
        )
        every { getWorkoutWithExercisesUseCase(workoutId) } returns flowOf(workout)
        
        viewModel = WorkoutExercisesViewModel(
            getWorkoutWithExercisesUseCase,
            calculateWorkoutVolumeUseCase,
            formatExerciseSummaryUseCase,
            formatWorkoutSummaryUseCase,
            configRepository,
            navigator
        )

        // When
        viewModel.loadWorkout(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(1, state.exercises.size)
        assertEquals("Pushups", state.exercises[0].canonicalName)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `onIntent ExerciseClicked navigates to ExerciseDetails`() = runTest {
        // Given
        viewModel = WorkoutExercisesViewModel(
            getWorkoutWithExercisesUseCase,
            calculateWorkoutVolumeUseCase,
            formatExerciseSummaryUseCase,
            formatWorkoutSummaryUseCase,
            configRepository,
            navigator
        )

        // When
        viewModel.onIntent(WorkoutExercisesIntent.ExerciseClicked("Bench Press"))

        // Then
        verify { navigator.navigateTo(Screen.ExerciseDetails("Bench Press")) }
    }

    @Test
    fun `onIntent NavigateBack navigates back`() = runTest {
        // Given
        viewModel = WorkoutExercisesViewModel(
            getWorkoutWithExercisesUseCase,
            calculateWorkoutVolumeUseCase,
            formatExerciseSummaryUseCase,
            formatWorkoutSummaryUseCase,
            configRepository,
            navigator
        )

        // When
        viewModel.onIntent(WorkoutExercisesIntent.NavigateBack)

        // Then
        verify { navigator.goBack() }
    }
}
