package com.scribblefit.feature.analytics.data.repository

import com.scribblefit.core.database.dao.InsightsCacheDao
import com.scribblefit.core.database.entity.InsightsCacheEntity
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import com.scribblefit.feature.analytics.domain.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY_HOME_SUGGESTION = "home_suggestion"

@Singleton
class AnalysisRepositoryImpl @Inject constructor(
    private val dao: InsightsCacheDao,
    private val json: Json
) : AnalysisRepository {

    override fun getHomeSuggestion(): Flow<AnalysisSuggestion?> =
        dao.getByKey(KEY_HOME_SUGGESTION).map { entity ->
            entity?.jsonData?.let { runCatching { json.decodeFromString<AnalysisSuggestion>(it) }.getOrNull() }
        }

    override fun getSummary(period: SummaryPeriod): Flow<AnalysisSummary?> =
        dao.getByKey("summary_${period.name.lowercase()}").map { entity ->
            entity?.jsonData?.let { runCatching { json.decodeFromString<AnalysisSummary>(it) }.getOrNull() }
        }

    override fun getExerciseInsight(exerciseId: String): Flow<ExerciseInsight?> =
        dao.getByKey("exercise_insight_$exerciseId").map { entity ->
            entity?.jsonData?.let { runCatching { json.decodeFromString<ExerciseInsight>(it) }.getOrNull() }
        }

    override suspend fun saveHomeSuggestion(suggestion: AnalysisSuggestion) {
        dao.upsert(InsightsCacheEntity(KEY_HOME_SUGGESTION, json.encodeToString(suggestion), System.currentTimeMillis()))
    }

    override suspend fun saveSummary(summary: AnalysisSummary) {
        val key = "summary_${summary.period.name.lowercase()}"
        dao.upsert(InsightsCacheEntity(key, json.encodeToString(summary), System.currentTimeMillis()))
    }

    override suspend fun saveExerciseInsight(insight: ExerciseInsight) {
        val key = "exercise_insight_${insight.exerciseId}"
        dao.upsert(InsightsCacheEntity(key, json.encodeToString(insight), System.currentTimeMillis()))
    }

    override suspend fun clearOldInsights() {
        dao.deleteAll()
    }
}
