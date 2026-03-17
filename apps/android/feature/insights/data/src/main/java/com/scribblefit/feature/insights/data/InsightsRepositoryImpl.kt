package com.scribblefit.feature.insights.data

import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class InsightsRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val coroutineDispatcher: CoroutineDispatcher
) : InsightsRepository {

    override fun getVolumeInsights(startDate: LocalDate, endDate: LocalDate): Flow<List<VolumeDataPoint>> {
        val startMillis = startDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val endMillis = endDate.atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant().toEpochMilli()

        return workoutDao.getWorkoutsWithAllDetailsInRange(startMillis, endMillis).map { workouts ->
            workouts.map { workoutWithDetails ->
                val totalVolume = workoutWithDetails.exercises.sumOf { exerciseWithDetails ->
                    exerciseWithDetails.sets.sumOf { set ->
                        (set.weight * set.reps).toDouble()
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

    override fun getFrequencyInsights(): Flow<FrequencyData> {
        return workoutDao.getAllWorkoutsWithAllDetails().map { workouts ->
            if (workouts.isEmpty()) return@map FrequencyData(0, 0f)

            val totalWorkouts = workouts.size
            val firstWorkoutDate = Instant.ofEpochMilli(workouts.first().workout.workoutDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
            val lastWorkoutDate = Instant.ofEpochMilli(workouts.last().workout.workoutDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()

            val weeks = ChronoUnit.WEEKS.between(firstWorkoutDate, lastWorkoutDate).coerceAtLeast(1L)
            val workoutsPerWeek = totalWorkouts.toFloat() / weeks.toFloat()

            FrequencyData(totalWorkouts, workoutsPerWeek)
        }
    }

    override fun getMuscleDistributionInsights(): Flow<List<MuscleGroupDistribution>> {
        return workoutDao.getAllWorkoutsWithAllDetails().map { workouts ->
            val muscleGroupCounts = mutableMapOf<String, Int>()
            var totalExercises = 0

            workouts.forEach { workoutWithDetails ->
                workoutWithDetails.exercises.forEach { exerciseWithDetails ->
                    val muscleGroup = exerciseWithDetails.exercise.muscleGroup
                    muscleGroupCounts[muscleGroup] = muscleGroupCounts.getOrDefault(muscleGroup, 0) + 1
                    totalExercises++
                }
            }

            if (totalExercises == 0) return@map emptyList()

            muscleGroupCounts.map { (muscleGroup, count) ->
                MuscleGroupDistribution(
                    muscleGroup = muscleGroup,
                    percentage = count.toFloat() / totalExercises.toFloat()
                )
            }.sortedByDescending { it.percentage }
        }
    }
}
