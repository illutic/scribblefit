package com.scribblefit.feature.insights.data

import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.entity.exercise.toDomain
import com.scribblefit.core.model.AIInsight
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.Instant

class InsightsRepositoryImpl(
    private val exerciseDao: ExerciseDao,
    private val llmEngine: LLMEngine,
    private val coroutineDispatcher: CoroutineDispatcher
) : InsightsRepository {
    override fun getVolumeInsights(
        startDate: Long,
        endDate: Long
    ): Flow<List<VolumeDataPoint>> =
        exerciseDao.getExercisesWithSetsInRange(startDate, endDate)
            .map { exercisesWithSets ->
                val exercises = exercisesWithSets.map { it.toDomain() }
                val volumeByDay = exercises
                    .groupBy { exercise -> exercise.createdAt }
                    .mapValues { (_, exercisesOnDay) ->
                        exercisesOnDay.sumOf { exercise ->
                            exercise.sets.sumOf { set ->
                                set.reps * (set.weight?.toDouble() ?: 0.0)
                            }
                        }
                    }

                volumeByDay
                    .map { (timestamp, volume) -> VolumeDataPoint(timestamp, volume.toFloat()) }
                    .sortedBy { it.date }
            }
            .flowOn(coroutineDispatcher)

    override fun getFrequencyInsights(
        startDate: Long,
        endDate: Long
    ): Flow<FrequencyData> =
        exerciseDao.getExercisesWithSetsInRange(startDate, endDate)
            .map { exercisesWithSets ->
                val exercises = exercisesWithSets.map { it.toDomain() }
                val totalExercises = exercises.size

                val startDate = Instant.ofEpochMilli(startDate)
                val endDate = Instant.ofEpochMilli(endDate)
                val weeks = Duration.between(startDate, endDate).toDays() / 7
                val workoutsPerWeek = totalExercises / weeks

                FrequencyData(
                    workoutsPerWeek = workoutsPerWeek,
                    totalExercises = totalExercises
                )
            }
            .flowOn(coroutineDispatcher)

    override fun getMuscleDistributionInsights(
        startDate: Long,
        endDate: Long
    ): Flow<List<MuscleGroupDistribution>> =
        exerciseDao.getExercisesWithSetsInRange(startDate, endDate)
            .map { exercisesWithSets ->
                val exercises = exercisesWithSets.map { it.toDomain() }
                val muscleGroupCounts = exercises
                    .map { it.muscleGroup }
                    .groupingBy { it }
                    .eachCount()

                val totalMuscleGroups = muscleGroupCounts.values.sum().toFloat()
                muscleGroupCounts.map { (muscleGroup, count) ->
                    MuscleGroupDistribution(
                        muscleGroup = muscleGroup,
                        percentage = (count / totalMuscleGroups) * 100f
                    )
                }.sortedByDescending { it.percentage }
            }
            .flowOn(coroutineDispatcher)

    private val aiInsightsCache = android.util.LruCache<String, List<AIInsight>>(10)

    override fun getAIOverview(
        startDate: Long,
        endDate: Long
    ): Flow<List<AIInsight>> {
        val exercisesWithSets = exerciseDao.getExercisesWithSetsInRange(startDate, endDate)
        val exercises = exercisesWithSets.map { list ->
            list.map { it.toDomain() }
        }

        return exercises
            .map { exercisesList ->
                if (exercisesList.isEmpty()) {
                    return@map emptyList()
                }

                // Cache key based on sorted exercise IDs (Issue 22) and their modified timestamps if any
                // If the user logs a new workout or modifies an existing one, the exercise IDs or the exercises list changes
                // Actually, just sorting exercise IDs is enough if exercises are immutable or if we include updated info
                // To be safe against weight/reps changes, we hash the entire exercises list content:
                val cacheKey = exercisesList.sortedBy { it.id }
                    .joinToString(",") { "${it.id}-${it.sets.hashCode()}" }

                val cached = aiInsightsCache.get(cacheKey)
                if (cached != null) {
                    cached
                } else {
                    val insights =
                        llmEngine.generateInsightsSummary(exercisesList).getOrDefault(emptyList())
                    aiInsightsCache.put(cacheKey, insights)
                    insights
                }
            }
            .flowOn(coroutineDispatcher)
    }
}
