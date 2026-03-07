package com.scribblefit.feature.ai.data.repository

import com.scribblefit.core.database.dao.InsightsCacheDao
import com.scribblefit.core.database.model.InsightsCacheEntity
import com.scribblefit.feature.ai.data.mapper.*
import com.scribblefit.feature.ai.domain.model.*
import com.scribblefit.feature.ai.domain.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalysisRepositoryImpl @Inject constructor(
    private val insightsCacheDao: InsightsCacheDao,
    private val json: Json
) : AnalysisRepository {

    override fun getHomeSuggestion(): Flow<AnalysisSuggestion?> {
        return insightsCacheDao.getInsightByKey(KEY_HOME_SUGGESTION).map { entity ->
            entity?.let { json.decodeFromString<SuggestionDto>(it.jsonData).toDomain() }
        }
    }

    override fun getSummary(period: SummaryPeriod): Flow<AnalysisSummary?> {
        val key = "${KEY_SUMMARY_PREFIX}_${period.name.lowercase()}"
        return insightsCacheDao.getInsightByKey(key).map { entity ->
            entity?.let { json.decodeFromString<SummaryDto>(it.jsonData).toDomain(period) }
        }
    }

    override fun getExerciseInsight(exerciseId: String): Flow<ExerciseInsight?> {
        val key = "${KEY_EXERCISE_PREFIX}_$exerciseId"
        return insightsCacheDao.getInsightByKey(key).map { entity ->
            entity?.let { json.decodeFromString<ExerciseInsightDto>(it.jsonData).toDomain(exerciseId) }
        }
    }

    override suspend fun saveHomeSuggestion(suggestion: AnalysisSuggestion) {
        val entity = InsightsCacheEntity(
            key = KEY_HOME_SUGGESTION,
            jsonData = json.encodeToString(suggestion.toDto()),
            createdAt = System.currentTimeMillis()
        )
        insightsCacheDao.upsertInsight(entity)
    }

    override suspend fun saveSummary(summary: AnalysisSummary) {
        val key = "${KEY_SUMMARY_PREFIX}_${summary.period.name.lowercase()}"
        val entity = InsightsCacheEntity(
            key = key,
            jsonData = json.encodeToString(summary.toDto()),
            createdAt = System.currentTimeMillis()
        )
        insightsCacheDao.upsertInsight(entity)
    }

    override suspend fun saveExerciseInsight(insight: ExerciseInsight) {
        val key = "${KEY_EXERCISE_PREFIX}_${insight.exerciseId}"
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

    companion object {
        private const val KEY_HOME_SUGGESTION = "home_suggestion"
        private const val KEY_SUMMARY_PREFIX = "summary"
        private const val KEY_EXERCISE_PREFIX = "exercise_insight"
    }
}
