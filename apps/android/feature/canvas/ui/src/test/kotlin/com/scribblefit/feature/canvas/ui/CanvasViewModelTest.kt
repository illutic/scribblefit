package com.scribblefit.feature.canvas.ui

import app.cash.turbine.test
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.navigation.NavState
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.canvas.domain.*
import com.scribblefit.feature.exercises.domain.usecase.FormatExerciseSummaryUseCase
import com.scribblefit.feature.insights.domain.usecase.GetAIOverviewUseCase
import com.scribblefit.feature.scribble.domain.usecase.AddRawScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.ConfirmScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetScribblesForDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.ManualEditScribbleUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class CanvasViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getScribblesForDateUseCase = mockk<GetScribblesForDateUseCase>()
    private val addRawScribbleUseCase = mockk<AddRawScribbleUseCase>()
    private val confirmScribbleUseCase = mockk<ConfirmScribbleUseCase>()
    private val deleteScribbleUseCase = mockk<DeleteScribbleUseCase>()
    private val parsePendingScribblesUseCase = mockk<ParsePendingScribblesUseCase>()
    private val manualEditScribbleUseCase = mockk<ManualEditScribbleUseCase>()
    private val createManualScribbleUseCase =
        mockk<com.scribblefit.feature.scribble.domain.usecase.CreateManualScribbleUseCase>()
    private val getAIInsightsUseCase = mockk<GetAIOverviewUseCase>()
    private val formatExerciseSummaryUseCase = mockk<FormatExerciseSummaryUseCase>()
    private val configRepository = mockk<ConfigRepository>()
    private val navigator = mockk<Navigator>()

    private lateinit var viewModel: CanvasViewModel

    private val defaultConfig = SystemConfig(
        summaryPrompt = "",
        suggestionPrompt = "",
        insightPrompt = "",
        parsePrompt = "",
        preferredLlmProvider = LLMProvider.LOCAL,
        updatedAt = 0,
        weightUnit = Weight.KGS,
        themePreference = ThemePreference.SYSTEM,
        isDynamicTheme = false
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { getScribblesForDateUseCase(any()) } returns flowOf(emptyList<Scribble>())
        coEvery { parsePendingScribblesUseCase(any()) } returns Unit
        coEvery { getAIInsightsUseCase(any<LocalDate>()) } returns Result.success(emptyList())
        every { configRepository.config } returns MutableStateFlow(defaultConfig)
        every { navigator.navState } returns MutableStateFlow(NavState())
        every { formatExerciseSummaryUseCase(any(), any()) } returns "Summary"

        viewModel = CanvasViewModel(
            getScribblesForDateUseCase = getScribblesForDateUseCase,
            addRawScribbleUseCase = addRawScribbleUseCase,
            confirmScribbleUseCase = confirmScribbleUseCase,
            deleteScribbleUseCase = deleteScribbleUseCase,
            parsePendingScribblesUseCase = parsePendingScribblesUseCase,
            manualEditScribbleUseCase = manualEditScribbleUseCase,
            createManualScribbleUseCase = createManualScribbleUseCase,
            getAIInsightsUseCase = getAIInsightsUseCase,
            formatExerciseSummaryUseCase = formatExerciseSummaryUseCase,
            configRepository = configRepository,
            navigator = navigator
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(LocalDate.now(), state.currentDate)
            assertTrue(state.scribbles.isEmpty())
            assertEquals(Weight.KGS, state.weightUnit)
        }
    }

    @Test
    fun `UpdateScribbleText updates currentScribbleText`() = runTest {
        viewModel.onIntent(CanvasIntent.UpdateScribbleText("Bench press"))

        viewModel.state.test {
            assertEquals("Bench press", awaitItem().currentScribbleText)
        }
    }

    @Test
    fun `OnPreviousDayClick updates currentDate and triggers parsing`() = runTest {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        viewModel.onIntent(CanvasIntent.OnPreviousDayClick)

        viewModel.state.test {
            assertEquals(yesterday, awaitItem().currentDate)
        }

        coVerify { parsePendingScribblesUseCase(yesterday) }
    }

    @Test
    fun `AddScribble calls use case and clears text`() = runTest {
        val text = "Squat 100kg 3x5"
        coEvery { addRawScribbleUseCase(any(), any()) } returns Result.success(Unit)

        viewModel.onIntent(CanvasIntent.UpdateScribbleText(text))
        viewModel.onIntent(CanvasIntent.AddScribble(text))

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { addRawScribbleUseCase(text, any()) }
        viewModel.state.test {
            assertEquals("", awaitItem().currentScribbleText)
        }
    }

    @Test
    fun `DeleteExercise calls use case`() = runTest {
        val scribble = Scribble(
            id = 1L,
            rawText = "Squat 100kg 3x5",
            status = ScribbleStatus.SUCCESS,
            createdAt = 0L,
            exercises = listOf(
                com.scribblefit.core.model.Exercise(
                    id = 10L,
                    canonicalName = "Squat",
                    muscleGroup = "Legs",
                    sets = emptyList()
                )
            )
        )

        coEvery { manualEditScribbleUseCase.deleteExercise(any(), any()) } returns Unit

        // Select the scribble
        viewModel.onIntent(CanvasIntent.ClickOnScribble(scribble))

        // Delete the exercise
        viewModel.onIntent(CanvasIntent.DeleteExercise(10L))

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { manualEditScribbleUseCase.deleteExercise(1L, 10L) }
    }

    @Test
    fun `AddSet calls use case`() = runTest {
        val scribble = Scribble(
            id = 1L,
            rawText = "Squat 100kg 3x5",
            status = ScribbleStatus.SUCCESS,
            createdAt = 0L,
            exercises = listOf(
                com.scribblefit.core.model.Exercise(
                    id = 10L,
                    canonicalName = "Squat",
                    muscleGroup = "Legs",
                    sets = emptyList()
                )
            )
        )

        coEvery { manualEditScribbleUseCase.addSet(any(), any()) } returns Unit

        // Select the scribble
        viewModel.onIntent(CanvasIntent.ClickOnScribble(scribble))

        // Add a set
        viewModel.onIntent(CanvasIntent.AddSet(10L))

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { manualEditScribbleUseCase.addSet(1L, 10L) }
    }

    @Test
    fun `SaveManualExercise calls use case and hides sheet`() = runTest {
        val name = "Bench Press"
        val muscleGroup = "Chest"
        val sets =
            listOf(com.scribblefit.core.model.Set(id = 0L, setNumber = 1, reps = 10, weight = 100f))

        coEvery { createManualScribbleUseCase(any(), any(), any(), any()) } returns Result.success(
            Unit
        )

        viewModel.onIntent(CanvasIntent.ShowAddExerciseSheet)
        viewModel.onIntent(CanvasIntent.SaveManualExercise(name, muscleGroup, sets, "Notes"))

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { createManualScribbleUseCase(name, muscleGroup, sets, any()) }
        viewModel.state.test {
            assertEquals(false, awaitItem().isAddExerciseSheetVisible)
        }
    }
}
