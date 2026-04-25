package com.scribblefit.feature.ledger.ui

import app.cash.turbine.test
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LocalConfig
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.navigation.NavState
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.scribble.domain.usecase.GetScribblesInRangeUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LedgerViewModelTest {

    private val getScribblesInRangeUseCase = mockk<GetScribblesInRangeUseCase>()
    private val configRepository = mockk<ConfigRepository>()
    private val navigator = mockk<Navigator>(relaxed = true)
    private val navStateFlow = MutableStateFlow(NavState())

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { navigator.navState } returns navStateFlow
        every { configRepository.config } returns MutableStateFlow(SystemConfig(localConfig = LocalConfig()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load completed scribbles for date range and sort descending`() =
        runTest(testDispatcher) {
            val scribbleOld = Scribble(1L, "bench 100x10", ScribbleStatus.COMPLETED, 1000L)
            val scribbleNew = Scribble(2L, "squat 120x8", ScribbleStatus.COMPLETED, 2000L)

            every { getScribblesInRangeUseCase(any(), any()) } returns flowOf(
                listOf(scribbleOld, scribbleNew)
            )

            val viewModel = LedgerViewModel(getScribblesInRangeUseCase, configRepository, navigator)

            viewModel.state.test {
                var state = awaitItem()
                if (state.scribbles.isEmpty()) {
                    state = awaitItem()
                }
                assertEquals(listOf(scribbleNew, scribbleOld), state.scribbles)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should navigate when NavigateToScreen intent is received`() = runTest(testDispatcher) {
        every { getScribblesInRangeUseCase(any(), any()) } returns flowOf(emptyList())
        val viewModel = LedgerViewModel(getScribblesInRangeUseCase, configRepository, navigator)

        viewModel.onIntent(LedgerIntent.NavigateToScreen(Screen.Canvas))

        verify { navigator.navigateTo(Screen.Canvas) }
    }

    @Test
    fun `should navigate to exercise details when NavigateToExerciseDetails intent is received`() =
        runTest(testDispatcher) {
            every { getScribblesInRangeUseCase(any(), any()) } returns flowOf(emptyList())
            val viewModel = LedgerViewModel(getScribblesInRangeUseCase, configRepository, navigator)

            viewModel.onIntent(LedgerIntent.NavigateToExerciseDetails(42L))

            verify { navigator.navigateTo(Screen.ExerciseDetails(42L)) }
        }

    @Test
    fun `should set selectedScribble when ScribbleTapped intent is received`() =
        runTest(testDispatcher) {
            val scribble = Scribble(1L, "bench 100x10", ScribbleStatus.COMPLETED, 1000L)
            every { getScribblesInRangeUseCase(any(), any()) } returns flowOf(listOf(scribble))

            val viewModel = LedgerViewModel(getScribblesInRangeUseCase, configRepository, navigator)

            viewModel.state.test {
                var state = awaitItem()
                if (state.scribbles.isEmpty()) {
                    state = awaitItem()
                }

                viewModel.onIntent(LedgerIntent.ScribbleTapped(1L))
                state = awaitItem()
                assertEquals(scribble, state.selectedScribble)

                viewModel.onIntent(LedgerIntent.DismissScribbleDetails)
                state = awaitItem()
                assertNull(state.selectedScribble)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
