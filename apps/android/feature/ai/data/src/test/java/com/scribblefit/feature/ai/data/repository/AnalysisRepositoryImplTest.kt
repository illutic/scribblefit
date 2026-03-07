package com.scribblefit.feature.ai.data.repository

import com.scribblefit.core.database.dao.InsightsCacheDao
import com.scribblefit.core.database.model.InsightsCacheEntity
import com.scribblefit.feature.ai.data.mapper.*
import com.scribblefit.core.ai.model.*
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.SuggestionType
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AnalysisRepositoryImplTest {

    private lateinit var insightsCacheDao: InsightsCacheDao
    private lateinit var repository: AnalysisRepositoryImpl
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        insightsCacheDao = mockk(relaxed = true)
        repository = AnalysisRepositoryImpl(insightsCacheDao, json)
    }

    @Test
    fun `getHomeSuggestion decodes JSON from DAO`() = runTest {
        // Given
        val suggestion = AnalysisSuggestion("Rest", "😴", SuggestionType.REST, 1000L)
        val entity = InsightsCacheEntity("home_suggestion", json.encodeToString(suggestion.toDto()), 1000L)
        coEvery { insightsCacheDao.getInsightByKey("home_suggestion") } returns flowOf(entity)

        // When
        val result = repository.getHomeSuggestion().first()

        // Then
        assertEquals(suggestion, result)
    }

    @Test
    fun `saveHomeSuggestion encodes JSON and upserts to DAO`() = runTest {
        // Given
        val suggestion = AnalysisSuggestion("Push", "🔥", SuggestionType.PATTERN, 1000L)

        // When
        repository.saveHomeSuggestion(suggestion)

        // Then
        coVerify { 
            insightsCacheDao.upsertInsight(match { 
                it.key == "home_suggestion" && it.jsonData.contains("Push") 
            }) 
        }
    }

    @Test
    fun `getSummary uses period in key`() = runTest {
        // Given
        val summary =
            AnalysisSummary(SummaryPeriod.MONTH, "Good month", emptyList(), emptyList(), 0.0, 1000L)
        val entity = InsightsCacheEntity("summary_month", json.encodeToString(summary.toDto()), 1000L)
        coEvery { insightsCacheDao.getInsightByKey("summary_month") } returns flowOf(entity)

        // When
        val result = repository.getSummary(SummaryPeriod.MONTH).first()

        // Then
        assertEquals(summary, result)
    }
}
