package com.scribblefit.feature.ledger.ui

import app.cash.turbine.test
import com.scribblefit.core.model.Workout
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.ledger.domain.usecase.GetWorkoutsInRangeUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class LedgerViewModelTest {

    private val getWorkoutsInRangeUseCase = mockk<GetWorkoutsInRangeUseCase>()
    private val navigator = mockk<Navigator>(relaxed = true)
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load workouts for current month by default and sort descending`() = runTest(testDispatcher) {
        val startOfMonth = LocalDate.now().withDayOfMonth(1)
        val today = LocalDate.now()
        
        val workoutOld = Workout(1L, 1000L, emptyList())
        val workoutNew = Workout(2L, 2000L, emptyList())
        
        every { getWorkoutsInRangeUseCase(startOfMonth, today) } returns flowOf(listOf(workoutOld, workoutNew))
        
        val viewModel = LedgerViewModel(getWorkoutsInRangeUseCase, navigator)
        
        viewModel.state.test {
            val initialState = awaitItem()
            if (initialState.workouts.isEmpty()) {
                val dataState = awaitItem()
                assertEquals(listOf(workoutNew, workoutOld), dataState.workouts)
            } else {
                assertEquals(listOf(workoutNew, workoutOld), initialState.workouts)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `should navigate to canvas when navigateToCanvas is called`() = runTest(testDispatcher) {
        every { getWorkoutsInRangeUseCase(any(), any()) } returns flowOf(emptyList())
        val viewModel = LedgerViewModel(getWorkoutsInRangeUseCase, navigator)
        
        viewModel.navigateToCanvas()
        
        verify { navigator.navigateTo(Screen.Canvas) }
    }
}
