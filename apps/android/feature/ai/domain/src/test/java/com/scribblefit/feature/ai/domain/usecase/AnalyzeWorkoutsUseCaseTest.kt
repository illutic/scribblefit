package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.core.ai.engine.AnalysisEngine
import com.scribblefit.core.ai.model.*
import com.scribblefit.core.ai.engine.AnalysisRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AnalyzeWorkoutsUseCaseTest {

    private lateinit var repository: AnalysisRepository
    private lateinit var engine: AnalysisEngine
    private lateinit var useCase: AnalyzeWorkoutsUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        engine = mockk()
        useCase = AnalyzeWorkoutsUseCase(repository, engine)
    }

    @Test
    fun `refreshHomeSuggestion calls engine and saves result to repository`() = runTest {
        // Given
        val context = "Hit chest 2 days ago"
        val suggestion = AnalysisSuggestion("Push Day", "💪", SuggestionType.PATTERN, 1000L)
        coEvery { engine.generateSuggestion(context) } returns Result.success(suggestion)

        // When
        useCase.refreshHomeSuggestion(context)

        // Then
        coVerify(exactly = 1) { engine.generateSuggestion(context) }
        coVerify(exactly = 1) { repository.saveHomeSuggestion(suggestion) }
    }

    @Test
    fun `refreshSummary calls engine and saves result to repository`() = runTest {
        // Given
        val period = SummaryPeriod.WEEK
        val data = "Workout data"
        val summary = AnalysisSummary(period, "Great week", emptyList(), emptyList(), 0.1, 1000L)
        coEvery { engine.generateSummary(period, data) } returns Result.success(summary)

        // When
        useCase.refreshSummary(period, data)

        // Then
        coVerify(exactly = 1) { engine.generateSummary(period, data) }
        coVerify(exactly = 1) { repository.saveSummary(summary) }
    }

    @Test
    fun `refreshExerciseInsight calls engine and saves result with mapped ID`() = runTest {
        // Given
        val exerciseId = "bench_press_id"
        val exerciseName = "Bench Press"
        val history = "Set history"
        val insight = ExerciseInsight("Bench Press", 225.0, true, InsightTrend.IMPROVING, "Great progress", 1000L)
        coEvery { engine.generateExerciseInsight(exerciseName, history) } returns Result.success(insight)

        // When
        useCase.refreshExerciseInsight(exerciseId, exerciseName, history)

        // Then
        coVerify(exactly = 1) { engine.generateExerciseInsight(exerciseName, history) }
        coVerify(exactly = 1) { 
            repository.saveExerciseInsight(match { it.exerciseId == exerciseId }) 
        }
    }
}
