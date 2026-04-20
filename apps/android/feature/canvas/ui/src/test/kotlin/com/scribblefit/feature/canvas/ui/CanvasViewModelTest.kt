package com.scribblefit.feature.canvas.ui

import app.cash.turbine.test
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.NavState
import com.scribblefit.feature.canvas.domain.*
import com.scribblefit.feature.insights.domain.usecase.GetAIOverviewUseCase
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
    private val parsePendingScribblesUseCase = mockk<ParsePendingScribblesUseCase>()
    private val addScribbleUseCase = mockk<AddScribbleUseCase>()
    private val confirmScribbleUseCase = mockk<ConfirmScribbleUseCase>()
    private val deleteScribbleUseCase = mockk<DeleteScribbleUseCase>()
    private val getAIOverviewUseCase = mockk<GetAIOverviewUseCase>()
    private val configRepository = mockk<ConfigRepository>()
    private val navigator = mockk<Navigator>()
    private val removeSetUseCase = mockk<com.scribblefit.feature.sets.domain.usecase.RemoveSetUseCase>()
    private val updateExerciseUseCase = mockk<com.scribblefit.feature.exercises.domain.usecase.UpdateExerciseUseCase>()
    private val updateSetRepsUseCase = mockk<com.scribblefit.feature.sets.domain.usecase.UpdateSetRepsUseCase>()
    private val updateSetWeightUseCase = mockk<com.scribblefit.feature.sets.domain.usecase.UpdateSetWeightUseCase>()
    private val reorderSetsUseCase = mockk<com.scribblefit.feature.sets.domain.usecase.ReorderSetsUseCase>()

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
        coEvery { getAIOverviewUseCase(any<LocalDate>()) } returns Result.success(emptyList())
        coEvery { getAIOverviewUseCase(any<LocalDate>(), any<LocalDate>()) } returns Result.success(emptyList())
        every { configRepository.config } returns MutableStateFlow(defaultConfig)
        every { navigator.navState } returns MutableStateFlow(NavState())
        
        viewModel = CanvasViewModel(
            getScribblesForDateUseCase = getScribblesForDateUseCase,
            parsePendingScribblesUseCase = parsePendingScribblesUseCase,
            addScribbleUseCase = addScribbleUseCase,
            confirmScribbleUseCase = confirmScribbleUseCase,
            deleteScribbleUseCase = deleteScribbleUseCase,
            getAIOverviewUseCase = getAIOverviewUseCase,
            configRepository = configRepository,
            navigator = navigator,
            removeSetUseCase = removeSetUseCase,
            updateExerciseUseCase = updateExerciseUseCase,
            updateSetRepsUseCase = updateSetRepsUseCase,
            updateSetWeightUseCase = updateSetWeightUseCase,
            reorderSetsUseCase = reorderSetsUseCase
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
    fun `OnDateSelected updates currentDate and triggers parsing`() = runTest {
        val selectedDate = LocalDate.now().minusDays(5)
        
        viewModel.onIntent(CanvasIntent.OnDateSelected(selectedDate))
        
        viewModel.state.test {
            assertEquals(selectedDate, awaitItem().currentDate)
        }
        
        coVerify { parsePendingScribblesUseCase(selectedDate) }
    }

    @Test
    fun `AddScribble calls use case and clears text`() = runTest {
        val text = "Squat 100kg 3x5"
        coEvery { addScribbleUseCase(any(), any()) } returns Unit
        
        viewModel.onIntent(CanvasIntent.UpdateScribbleText(text))
        viewModel.onIntent(CanvasIntent.AddScribble(text))
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { addScribbleUseCase(text, any()) }
        viewModel.state.test {
            assertEquals("", awaitItem().currentScribbleText)
        }
    }

    @Test
    fun `DeleteSet removes set from state and calls use case if completed`() = runTest {
        val scribble = Scribble(
            id = 1L,
            rawText = "Squat 100kg 3x5",
            status = ScribbleStatus.COMPLETED,
            createdAt = 0L,
            exercises = listOf(
                com.scribblefit.core.model.Exercise(
                    id = 1L,
                    canonicalName = "Squat",
                    muscleGroup = "Legs",
                    sets = listOf(
                        com.scribblefit.core.model.Set(id = 1L, setNumber = 1, weight = 100f, reps = 5),
                        com.scribblefit.core.model.Set(id = 2L, setNumber = 2, weight = 100f, reps = 5)
                    )
                )
            )
        )
        
        coEvery { removeSetUseCase(any()) } just Runs
        every { reorderSetsUseCase(any()) } answers {
            val sets = firstArg<List<com.scribblefit.core.model.Set>>()
            sets.mapIndexed { index, set -> set.copy(setNumber = index + 1) }
        }
        
        // Open the scribble dialog
        viewModel.onIntent(CanvasIntent.ClickOnScribble(scribble))
        
        // Delete the first set
        viewModel.onIntent(CanvasIntent.DeleteSet(exerciseId = 1L, setId = 1L))
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { removeSetUseCase(1L) }
        
        viewModel.state.test {
            val state = awaitItem()
            val updatedScribble = state.selectedScribble
            assertEquals(1, updatedScribble?.exercises?.first()?.sets?.size)
            assertEquals(2L, updatedScribble?.exercises?.first()?.sets?.first()?.id)
            assertEquals(1, updatedScribble?.exercises?.first()?.sets?.first()?.setNumber)
        }
    }

    @Test
    fun `UpdateSetWeight updates weight in state and calls use case if completed`() = runTest {
        val scribble = Scribble(
            id = 1L,
            rawText = "Squat 100kg 3x5",
            status = ScribbleStatus.COMPLETED,
            createdAt = 0L,
            exercises = listOf(
                com.scribblefit.core.model.Exercise(
                    id = 1L,
                    canonicalName = "Squat",
                    muscleGroup = "Legs",
                    sets = listOf(
                        com.scribblefit.core.model.Set(id = 1L, setNumber = 1, weight = 100f, reps = 5)
                    )
                )
            )
        )
        
        coEvery { updateSetWeightUseCase(any(), any()) } returns Result.success(Unit)
        
        // Open the scribble dialog
        viewModel.onIntent(CanvasIntent.ClickOnScribble(scribble))
        
        // Update weight to 110
        viewModel.onIntent(CanvasIntent.UpdateSetWeight(exerciseId = 1L, setId = 1L, newWeight = "110"))
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { updateSetWeightUseCase(1L, 110f) }
        
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(110f, state.selectedScribble?.exercises?.first()?.sets?.first()?.weight)
        }
    }

    @Test
    fun `UpdateSetWeight with empty string sets weight to null`() = runTest {
        val scribble = Scribble(
            id = 1L,
            rawText = "Squat 100kg 3x5",
            status = ScribbleStatus.COMPLETED,
            createdAt = 0L,
            exercises = listOf(
                com.scribblefit.core.model.Exercise(
                    id = 1L,
                    canonicalName = "Squat",
                    muscleGroup = "Legs",
                    sets = listOf(
                        com.scribblefit.core.model.Set(id = 1L, setNumber = 1, weight = 100f, reps = 5)
                    )
                )
            )
        )
        
        coEvery { updateSetWeightUseCase(any(), any()) } returns Result.success(Unit)
        
        // Open the scribble dialog
        viewModel.onIntent(CanvasIntent.ClickOnScribble(scribble))
        
        // Update weight to empty
        viewModel.onIntent(CanvasIntent.UpdateSetWeight(exerciseId = 1L, setId = 1L, newWeight = ""))
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { updateSetWeightUseCase(1L, null) }
        
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(null, state.selectedScribble?.exercises?.first()?.sets?.first()?.weight)
        }
    }
}
