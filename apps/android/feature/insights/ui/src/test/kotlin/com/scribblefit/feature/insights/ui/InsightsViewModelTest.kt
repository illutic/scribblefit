package com.scribblefit.feature.insights.ui

import app.cash.turbine.test
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.navigation.NavState
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.usecase.GetAIOverviewUseCase
import com.scribblefit.feature.insights.domain.usecase.GetFrequencyInsightsUseCase
import com.scribblefit.feature.insights.domain.usecase.GetMuscleDistributionInsightsUseCase
import com.scribblefit.feature.insights.domain.usecase.GetVolumeInsightsUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getVolumeInsightsUseCase = mockk<GetVolumeInsightsUseCase>()
    private val getFrequencyInsightsUseCase = mockk<GetFrequencyInsightsUseCase>()
    private val getMuscleDistributionInsightsUseCase = mockk<GetMuscleDistributionInsightsUseCase>()
    private val getAIOverviewUseCase = mockk<GetAIOverviewUseCase>()
    private val navigator = mockk<Navigator>()

    private lateinit var viewModel: InsightsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { getVolumeInsightsUseCase(any(), any()) } returns Result.success(emptyList())
        coEvery { getFrequencyInsightsUseCase(any(), any()) } returns Result.success(FrequencyData(0))
        coEvery { getMuscleDistributionInsightsUseCase(any(), any()) } returns Result.success(emptyList())
        coEvery { getAIOverviewUseCase(any<CurrentDate>(), any<CurrentDate>()) } returns Result.success(
            emptyList()
        )
        every { navigator.navState } returns MutableStateFlow(NavState())

        viewModel = InsightsViewModel(
            getVolumeInsightsUseCase = getVolumeInsightsUseCase,
            getFrequencyInsightsUseCase = getFrequencyInsightsUseCase,
            getMuscleDistributionInsightsUseCase = getMuscleDistributionInsightsUseCase,
            getAIOverviewUseCase = getAIOverviewUseCase,
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
            assertFalse(state.isLoading)
            assertEquals(InsightsPeriod.WEEKLY, state.selectedPeriod)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Refresh intent triggers reload`() = runTest {
        viewModel.state.test {
            awaitItem() // Consume initial
            
            viewModel.onIntent(InsightsIntent.Refresh)
            
            // Just verify side effect, might not emit if timestamp is same or combine deduplicates
            coVerify(atLeast = 1) { getVolumeInsightsUseCase(any(), any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SelectPeriod updates dates and triggers reload`() = runTest {
        viewModel.state.test {
            awaitItem() // Initial
            
            viewModel.onIntent(InsightsIntent.SelectPeriod(InsightsPeriod.MONTHLY))
            
            val state = awaitItem()
            assertEquals(InsightsPeriod.MONTHLY, state.selectedPeriod)
            
            coVerify(atLeast = 1) { getVolumeInsightsUseCase(any(), any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `AI Overview is triggered when period changes`() = runTest {
        viewModel.state.test {
            awaitItem() // Initial
            
            viewModel.onIntent(InsightsIntent.SelectPeriod(InsightsPeriod.DAILY))
            
            // Verify call happens
            coVerify(atLeast = 1) { getAIOverviewUseCase(any<CurrentDate>(), any<CurrentDate>()) }
            cancelAndIgnoreRemainingEvents()
        }
    }
}
