package com.scribblefit.feature.insights.data

import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.InsightType
import com.scribblefit.feature.ai.domain.LLMEngineProxy
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class InsightsRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val llmEngineProxy: LLMEngineProxy,
    private val coroutineDispatcher: CoroutineDispatcher
) : InsightsRepository {

    override fun getVolumeInsights(
        startDate: Long,
        endDate: Long
    ): Flow<List<VolumeDataPoint>> {
        return workoutDao.getWorkoutsWithAllDetailsInRange(startDate, endDate).map { workouts ->
            workouts.map { workoutWithDetails ->
                val totalVolume = workoutWithDetails.exercises.sumOf { exerciseWithDetails ->
                    exerciseWithDetails.sets.sumOf { set ->
                        ((set.weight ?: 0f) * set.reps).toDouble()
                    }
                }.toFloat()

                VolumeDataPoint(
                    date = Instant.ofEpochMilli(workoutWithDetails.workout.workoutDate)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate(),
                    volume = totalVolume
                )
            }
        }
    }

    override fun getFrequencyInsights(
        startDate: Long,
        endDate: Long
    ): Flow<FrequencyData> {
        return workoutDao.getWorkoutsWithAllDetailsInRange(startDate, endDate).map { workouts ->
            if (workouts.isEmpty()) return@map FrequencyData(0, 0f, 0)

            val totalWorkouts = workouts.size
            val totalExercises = workouts.sumOf { it.exercises.size }
            val firstWorkoutDate = Instant.ofEpochMilli(workouts.first().workout.workoutDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
            val lastWorkoutDate = Instant.ofEpochMilli(workouts.last().workout.workoutDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()

            val weeks =
                ChronoUnit.WEEKS.between(firstWorkoutDate, lastWorkoutDate).coerceAtLeast(1L)
            val workoutsPerWeek = totalWorkouts.toFloat() / weeks.toFloat()

            FrequencyData(totalWorkouts, workoutsPerWeek, totalExercises)
        }
    }

    override fun getMuscleDistributionInsights(
        startDate: Long,
        endDate: Long
    ): Flow<List<MuscleGroupDistribution>> {
        return workoutDao.getWorkoutsWithAllDetailsInRange(startDate, endDate).map { workouts ->
            val muscleGroupCounts = mutableMapOf<String, Int>()
            var totalExercises = 0

            workouts.forEach { workoutWithDetails ->
                workoutWithDetails.exercises.forEach { exerciseWithDetails ->
                    val muscleGroup = exerciseWithDetails.exercise.muscleGroup
                    muscleGroupCounts[muscleGroup] =
                        muscleGroupCounts.getOrDefault(muscleGroup, 0) + 1
                    totalExercises++
                }
            }

            if (totalExercises == 0) return@map emptyList()

            muscleGroupCounts.map { (muscleGroup, count) ->
                MuscleGroupDistribution(
                    muscleGroup = muscleGroup,
                    percentage = (count.toFloat() / totalExercises.toFloat()) * 100f
                )
            }.sortedByDescending { it.percentage }
        }
    }

    override suspend fun getAIOverview(exercises: List<Exercise>): Result<List<AIInsight>> =
        withContext(coroutineDispatcher) {
            try {
                val llmEngine = llmEngineProxy.underlyingEngine.first()
                if (exercises.isEmpty()) {
                    return@withContext Result.success(
                        listOf(
                            AIInsight(
                                InsightType.SUMMARY,
                                "Start your session by scribbling your first workout!"
                            )
                        )
                    )
                }

                llmEngine.generateInsightsSummary(exercises)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
