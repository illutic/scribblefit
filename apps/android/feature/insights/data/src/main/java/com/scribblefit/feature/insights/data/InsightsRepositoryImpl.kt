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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
            .flowOn(coroutineDispatcher)
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

    override fun getFrequencyInsights(
        startDate: Long,
        endDate: Long
    ): Flow<FrequencyData> =
        exerciseDao.getExercisesWithSetsInRange(startDate, endDate)
            .flowOn(coroutineDispatcher)
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

    override fun getMuscleDistributionInsights(
        startDate: Long,
        endDate: Long
    ): Flow<List<MuscleGroupDistribution>> =
        exerciseDao.getExercisesWithSetsInRange(startDate, endDate)
            .flowOn(coroutineDispatcher)
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

    override suspend fun getAIOverview(
        startDate: Long,
        endDate: Long
    ): List<AIInsight> = withContext(coroutineDispatcher) {
        val exercisesWithSets = exerciseDao.getExercisesWithSetsInRange(startDate, endDate)
            .firstOrNull() ?: return@withContext emptyList()
        val exercises = exercisesWithSets.map { it.toDomain() }

        if (exercises.isEmpty()) return@withContext emptyList()
        llmEngine.generateInsightsSummary(exercises).getOrThrow()
    }
}
