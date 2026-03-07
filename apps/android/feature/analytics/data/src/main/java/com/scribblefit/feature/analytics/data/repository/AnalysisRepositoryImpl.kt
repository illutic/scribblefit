package com.scribblefit.feature.analytics.data.repository

import com.scribblefit.core.ai.model.*
import com.scribblefit.feature.analytics.domain.repository.AnalysisRepository
import com.scribblefit.feature.ai.data.mapper.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.scribblefit.core.database.dao.InsightsCacheDao
import com.scribblefit.core.database.model.InsightsCacheEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalysisRepositoryImpl @Inject constructor(
    private val insightsCacheDao: InsightsCacheDao,
    private val json: Json
) : AnalysisRepository {

    override fun getHomeSuggestion(): Flow<AnalysisSuggestion?> {
        return insightsCacheDao.getInsightByKey("home_suggestion").map { entity ->
            entity?.let { json.decodeFromString<SuggestionDto>(it.jsonData).toDomain() }
        }
    }

    override fun getSummary(period: SummaryPeriod): Flow<AnalysisSummary?> {
        val key = "summary_${period.name.lowercase()}"
        return insightsCacheDao.getInsightByKey(key).map { entity ->
            entity?.let { json.decodeFromString<SummaryDto>(it.jsonData).toDomain(period) }
        }
    }

    override fun getExerciseInsight(exerciseId: String): Flow<ExerciseInsight?> {
        val key = "exercise_insight_$exerciseId"
        return insightsCacheDao.getInsightByKey(key).map { entity ->
            entity?.let { json.decodeFromString<ExerciseInsightDto>(it.jsonData).toDomain(exerciseId) }
        }
    }

    override suspend fun saveHomeSuggestion(suggestion: AnalysisSuggestion) {
        val entity = InsightsCacheEntity(
            key = "home_suggestion",
            jsonData = json.encodeToString(suggestion.toDto()),
            createdAt = System.currentTimeMillis()
        )
        insightsCacheDao.upsertInsight(entity)
    }

    override suspend fun saveSummary(summary: AnalysisSummary) {
        val key = "summary_${summary.period.name.lowercase()}"
        val entity = InsightsCacheEntity(
            key = key,
            jsonData = json.encodeToString(summary.toDto()),
            createdAt = System.currentTimeMillis()
        )
        insightsCacheDao.upsertInsight(entity)
    }

    override suspend fun saveExerciseInsight(insight: ExerciseInsight) {
        val key = "exercise_insight_${insight.exerciseId}"
        val entity = InsightsCacheEntity(
            key = key,
            jsonData = json.encodeToString(insight.toDto()),
            createdAt = System.currentTimeMillis()
        )
        insightsCacheDao.upsertInsight(entity)
    }

    override suspend fun clearOldInsights() {
        insightsCacheDao.deleteAll()
    }
}
