package com.scribblefit.feature.canvas.ui

import app.cash.turbine.test
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.LocalConfig
import com.scribblefit.core.config.domain.RemoteConfig
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.ExerciseTrends
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Set
import com.scribblefit.core.model.TrendDirection
import com.scribblefit.core.navigation.NavState
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.canvas.domain.ParsePendingScribblesUseCase
import com.scribblefit.feature.canvas.domain.RemoveExerciseFromScribbleUseCase
import com.scribblefit.feature.exercises.domain.usecase.CalculateTrendsUseCase
import com.scribblefit.feature.exercises.domain.usecase.FormatExerciseSummaryUseCase
import com.scribblefit.feature.exercises.domain.usecase.RemoveExerciseUseCase
import com.scribblefit.feature.exercises.domain.usecase.UpdateExerciseUseCase
import com.scribblefit.feature.insights.domain.usecase.GetAIOverviewUseCase
import com.scribblefit.feature.scribble.domain.usecase.AddScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.ConfirmScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.CreateManualScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetScribblesForDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import com.scribblefit.feature.sets.domain.usecase.AddSetToExerciseUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class CanvasViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getScribblesForDateUseCase = mockk<GetScribblesForDateUseCase>()
    private val addScribbleUseCase = mockk<AddScribbleUseCase>()
    private val confirmScribbleUseCase = mockk<ConfirmScribbleUseCase>()
    private val deleteScribbleUseCase = mockk<RemoveScribbleUseCase>()
    private val updateExerciseUseCase = mockk<UpdateExerciseUseCase>()
    private val removeExerciseFromScribbleUseCase = mockk<RemoveExerciseFromScribbleUseCase>()
    private val addSetToExerciseUseCase = mockk<AddSetToExerciseUseCase>()
    private val parsePendingScribblesUseCase = mockk<ParsePendingScribblesUseCase>()
    private val createManualScribbleUseCase = mockk<CreateManualScribbleUseCase>()
    private val getAIInsightsUseCase = mockk<GetAIOverviewUseCase>()
    private val formatExerciseSummaryUseCase = mockk<FormatExerciseSummaryUseCase>()
    private val calculateTrendsUseCase = mockk<CalculateTrendsUseCase>()
    private val configRepository = mockk<ConfigRepository>()
    private val navigator = mockk<Navigator>()

    private lateinit var viewModel: CanvasViewModel

    private val defaultConfig = SystemConfig(
        localConfig = LocalConfig(
            preferredLlmProvider = LLMProvider.LOCAL,
            weightUnit = Weight.KGS,
            themePreference = ThemePreference.SYSTEM,
            isDynamicTheme = false
        ),
        remoteConfig = RemoteConfig()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val aiResult: Result<List<AIInsight>> = Result.success(emptyList())
        val trendsResult: Result<ExerciseTrends> = Result.success(
            ExerciseTrends(0f, 0f, 0f, TrendDirection.STABLE)
        )

        coEvery { getScribblesForDateUseCase(any()) } returns flowOf(emptyList<Scribble>())
        coEvery { parsePendingScribblesUseCase(any()) } returns Unit
        coEvery { getAIInsightsUseCase(any<CurrentDate>(), any<Long>()) } returns aiResult
        every { configRepository.config } returns MutableStateFlow(defaultConfig)
        every { navigator.navState } returns MutableStateFlow(NavState())
        every { formatExerciseSummaryUseCase(any(), any()) } returns "Summary"
        coEvery { calculateTrendsUseCase(any()) } returns trendsResult

        viewModel = CanvasViewModel(
            getScribblesForDateUseCase = getScribblesForDateUseCase,
            addScribbleUseCase = addScribbleUseCase,
            confirmScribbleUseCase = confirmScribbleUseCase,
            deleteScribbleUseCase = deleteScribbleUseCase,
            updateExerciseUseCase = updateExerciseUseCase,
            removeExerciseFromScribbleUseCase = removeExerciseFromScribbleUseCase,
            addSetToExerciseUseCase = addSetToExerciseUseCase,
            parsePendingScribblesUseCase = parsePendingScribblesUseCase,
            createManualScribbleUseCase = createManualScribbleUseCase,
            getAIInsightsUseCase = getAIInsightsUseCase,
            formatExerciseSummaryUseCase = formatExerciseSummaryUseCase,
            calculateTrendsUseCase = calculateTrendsUseCase,
            configRepository = configRepository,
            navigator = navigator
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest(testDispatcher) {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(LocalDate.now(), state.currentDate)
            assertTrue(state.scribbles.isEmpty())
            assertEquals(Weight.KGS, state.weightUnit)
        }
    }

    @Test
    fun `UpdateScribbleText updates currentScribbleText`() = runTest(testDispatcher) {
        viewModel.onIntent(CanvasIntent.UpdateScribbleText("Bench press"))

        viewModel.state.test {
            val state = expectMostRecentItem()
            assertEquals("Bench press", state.currentScribbleText)
        }
    }

    @Test
    fun `OnPreviousDayClick updates currentDate and triggers parsing`() = runTest(testDispatcher) {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        viewModel.onIntent(CanvasIntent.OnPreviousDayClick)

        viewModel.state.test {
            val state = expectMostRecentItem()
            assertEquals(yesterday, state.currentDate)
        }

        coVerify { parsePendingScribblesUseCase(any()) }
    }

    @Test
    fun `AddScribble calls use case and clears text`() = runTest(testDispatcher) {
        val text = "Squat 100kg 3x5"
        coEvery { addScribbleUseCase(any(), any()) } returns Unit

        viewModel.onIntent(CanvasIntent.UpdateScribbleText(text))
        viewModel.onIntent(CanvasIntent.AddScribble(text))

        coVerify { addScribbleUseCase(text, any()) }
        viewModel.state.test {
            val state = expectMostRecentItem()
            assertEquals("", state.currentScribbleText)
        }
    }

    @Test
    fun `DeleteExercise calls use case`() = runTest(testDispatcher) {
        val scribble = Scribble(
            id = 1L,
            rawText = "Squat 100kg 3x5",
            status = ScribbleStatus.SUCCESS,
            createdAt = 0L,
            exercises = listOf(
                Exercise(
                    id = 10L,
                    canonicalName = "Squat",
                    muscleGroup = "Legs",
                    sets = emptyList<com.scribblefit.core.model.Set>(),
                    createdAt = 0L
                )
            )
        )

        val removeResult: Result<Unit> = Result.success(Unit)
        coEvery { removeExerciseFromScribbleUseCase(any(), any()) } returns removeResult

        // Select the scribble
        viewModel.onIntent(CanvasIntent.ClickOnScribble(scribble))

        // Delete the exercise
        viewModel.onIntent(CanvasIntent.DeleteExercise(10L))

        coVerify { removeExerciseFromScribbleUseCase(10L, 1L) }
    }

    @Test
    fun `AddSet calls use case`() = runTest(testDispatcher) {
        val exercise = Exercise(
            id = 10L,
            canonicalName = "Squat",
            muscleGroup = "Legs",
            sets = emptyList<com.scribblefit.core.model.Set>(),
            createdAt = 0L
        )
        val scribble = Scribble(
            id = 1L,
            rawText = "Squat 100kg 3x5",
            status = ScribbleStatus.SUCCESS,
            createdAt = 0L,
            exercises = listOf(exercise)
        )

        val addSetResult: Result<Long> = Result.success(1L)
        coEvery { addSetToExerciseUseCase(any()) } returns addSetResult

        // Select the scribble
        viewModel.onIntent(CanvasIntent.ClickOnScribble(scribble))

        // Add a set
        viewModel.onIntent(CanvasIntent.AddSet(10L))

        coVerify { addSetToExerciseUseCase(exercise) }
    }

    @Test
    fun `SaveManualExercise calls use case and hides sheet`() = runTest(testDispatcher) {
        val name = "Bench Press"
        val muscleGroup = "Chest"
        val sets = listOf(Set(id = 0L, setNumber = 1, reps = 10, weight = 100f))

        val createResult: Result<Long> = Result.success(1L)
        coEvery { createManualScribbleUseCase(any(), any(), any(), any()) } returns createResult

        viewModel.onIntent(CanvasIntent.ShowAddExerciseSheet)
        viewModel.onIntent(CanvasIntent.SaveManualExercise(name, muscleGroup, sets, "Notes"))

        coVerify { createManualScribbleUseCase(name, muscleGroup, sets, any()) }
        viewModel.state.test {
            val state = expectMostRecentItem()
            assertEquals(false, state.isAddExerciseSheetVisible)
        }
    }

    @Test
    fun `selectedScribble updates when underlying scribble list changes`() = runTest(testDispatcher) {
        val scribbleId = 1L
        val exerciseId = 10L
        val initialExercise = Exercise(
            id = exerciseId,
            canonicalName = "Squat",
            muscleGroup = "Legs",
            sets = emptyList(),
            createdAt = 0L
        )
        val initialScribble = Scribble(
            id = scribbleId,
            rawText = "Squat",
            status = ScribbleStatus.SUCCESS,
            createdAt = 0L,
            exercises = listOf(initialExercise)
        )

        val scribblesFlow = MutableStateFlow(listOf(initialScribble))
        coEvery { getScribblesForDateUseCase(any()) } returns scribblesFlow

        // Initialize VM again to pick up the flow
        viewModel = CanvasViewModel(
            getScribblesForDateUseCase = getScribblesForDateUseCase,
            addScribbleUseCase = addScribbleUseCase,
            confirmScribbleUseCase = confirmScribbleUseCase,
            deleteScribbleUseCase = deleteScribbleUseCase,
            updateExerciseUseCase = updateExerciseUseCase,
            removeExerciseFromScribbleUseCase = removeExerciseFromScribbleUseCase,
            addSetToExerciseUseCase = addSetToExerciseUseCase,
            parsePendingScribblesUseCase = parsePendingScribblesUseCase,
            createManualScribbleUseCase = createManualScribbleUseCase,
            getAIInsightsUseCase = getAIInsightsUseCase,
            formatExerciseSummaryUseCase = formatExerciseSummaryUseCase,
            calculateTrendsUseCase = calculateTrendsUseCase,
            configRepository = configRepository,
            navigator = navigator
        )

        viewModel.state.test {
            awaitItem() // Skip initial

            // Select the scribble
            viewModel.onIntent(CanvasIntent.ClickOnScribble(initialScribble))
            var state = awaitItem()
            assertEquals("Squat", state.selectedScribble?.exercises?.get(0)?.canonicalName)

            // Simulate update in DB
            val updatedExercise = initialExercise.copy(canonicalName = "Back Squat")
            val updatedScribble = initialScribble.copy(exercises = listOf(updatedExercise))
            scribblesFlow.value = listOf(updatedScribble)

            state = awaitItem()
            assertEquals("Back Squat", state.selectedScribble?.exercises?.get(0)?.canonicalName)
        }
    }
}
