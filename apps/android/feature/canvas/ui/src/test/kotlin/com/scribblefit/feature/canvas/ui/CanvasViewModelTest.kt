package com.scribblefit.feature.canvas.ui

import app.cash.turbine.test
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.LocalConfig
import com.scribblefit.core.config.domain.RemoteConfig
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.ExerciseTrends
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.TrendDirection
import com.scribblefit.core.navigation.NavState
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.canvas.domain.ParsePendingScribblesUseCase
import com.scribblefit.feature.canvas.domain.RemoveExerciseFromScribbleUseCase
import com.scribblefit.feature.canvas.domain.UpdateScribbleExerciseUseCase
import com.scribblefit.feature.exercises.domain.usecase.AddExerciseUseCase
import com.scribblefit.feature.exercises.domain.usecase.CalculateTrendsUseCase
import com.scribblefit.feature.exercises.domain.usecase.FormatExerciseSummaryUseCase
import com.scribblefit.feature.insights.domain.usecase.GetAIOverviewUseCase
import com.scribblefit.feature.scribble.domain.usecase.AddScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.ConfirmScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.CreateManualScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetScribblesForDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import com.scribblefit.feature.sets.domain.usecase.AddSetToExerciseUseCase
import io.mockk.coEvery
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
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class CanvasViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getScribblesForDateUseCase = mockk<GetScribblesForDateUseCase>()
    private val addScribbleUseCase = mockk<AddScribbleUseCase>()
    private val confirmScribbleUseCase = mockk<ConfirmScribbleUseCase>()
    private val deleteScribbleUseCase = mockk<RemoveScribbleUseCase>()
    private val addExerciseUseCase = mockk<AddExerciseUseCase>()
    private val updateScribbleExerciseUseCase = mockk<UpdateScribbleExerciseUseCase>()
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

        val trendsResult: Result<ExerciseTrends> = Result.success(
            ExerciseTrends(0f, 0f, 0f, TrendDirection.STABLE)
        )

        coEvery { getScribblesForDateUseCase(any()) } returns flowOf(emptyList<Scribble>())
        coEvery { parsePendingScribblesUseCase(any()) } returns Unit
        coEvery {
            getAIInsightsUseCase(
                any<CurrentDate>(),
                any<Long>()
            )
        } returns flowOf(emptyList())
        every { configRepository.config } returns MutableStateFlow(defaultConfig)
        every { navigator.navState } returns MutableStateFlow(NavState())
        every { formatExerciseSummaryUseCase(any(), any()) } returns "Summary"
        coEvery { calculateTrendsUseCase(any()) } returns trendsResult

        viewModel = CanvasViewModel(
            getScribblesForDateUseCase = getScribblesForDateUseCase,
            addScribbleUseCase = addScribbleUseCase,
            confirmScribbleUseCase = confirmScribbleUseCase,
            deleteScribbleUseCase = deleteScribbleUseCase,
            removeExerciseFromScribbleUseCase = removeExerciseFromScribbleUseCase,
            addSetToExerciseUseCase = addSetToExerciseUseCase,
            parsePendingScribblesUseCase = parsePendingScribblesUseCase,
            createManualScribbleUseCase = createManualScribbleUseCase,
            getAIInsightsUseCase = getAIInsightsUseCase,
            formatExerciseSummaryUseCase = formatExerciseSummaryUseCase,
            calculateTrendsUseCase = calculateTrendsUseCase,
            configRepository = configRepository,
            navigator = navigator,
            addExerciseUseCase = addExerciseUseCase,
            updateScribbleExerciseUseCase = updateScribbleExerciseUseCase
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
            assertEquals(LocalDateTime.now().toLocalDate(), state.currentDate.toLocalDate())
            assertTrue(state.scribbles.isEmpty())
            assertEquals(Weight.KGS, state.weightUnit)
        }
    }
}
