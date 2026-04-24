package com.scribblefit.feature.ledger.ui

import app.cash.turbine.test
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.navigation.NavState
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.exercises.domain.usecase.GetExercisesInRangeUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LedgerViewModelTest {

    private val getExercisesInRangeUseCase = mockk<GetExercisesInRangeUseCase>()
    private val navigator = mockk<Navigator>(relaxed = true)
    private val navStateFlow = MutableStateFlow(NavState())

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { navigator.navState } returns navStateFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load exercises for last 30 days by default and sort descending`() =
        runTest(testDispatcher) {
            val exerciseOld = Exercise(1L, "Bench Press", "Chest", emptyList(), 1000L)
            val exerciseNew = Exercise(2L, "Squat", "Legs", emptyList(), 2000L)

            coEvery { getExercisesInRangeUseCase(any(), any()) } returns Result.success(listOf(exerciseOld, exerciseNew))

            val viewModel = LedgerViewModel(getExercisesInRangeUseCase, navigator)

            viewModel.state.test {
                // The first item might be the initial state
                var state = awaitItem()
                if (state.exercises.isEmpty()) {
                    state = awaitItem()
                }
                assertEquals(listOf(exerciseNew, exerciseOld), state.exercises)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should navigate when NavigateToScreen intent is received`() = runTest(testDispatcher) {
        coEvery { getExercisesInRangeUseCase(any(), any()) } returns Result.success(emptyList())
        val viewModel = LedgerViewModel(getExercisesInRangeUseCase, navigator)

        viewModel.onIntent(LedgerIntent.NavigateToScreen(Screen.Canvas))

        verify { navigator.navigateTo(Screen.Canvas) }
    }
}
