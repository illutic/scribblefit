package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.Calculations
import com.scribblefit.core.model.ExerciseDetails
import com.scribblefit.core.model.ExerciseHistorySession
import com.scribblefit.core.model.ExerciseTrends
import com.scribblefit.core.model.TrendDirection
import com.scribblefit.core.model.WeeklyStats
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

class GetExerciseDetailsUseCase(
    private val workoutRepository: WorkoutRepository
) {
    operator fun invoke(exerciseName: String): Flow<ExerciseDetails> {
        return workoutRepository.getWorkoutsWithExercise(exerciseName).map { workouts ->
            val history = workouts.sortedByDescending { it.date }.flatMap { workout ->
                workout.exercises.filter { it.canonicalName == exerciseName }.map { exercise ->
                    ExerciseHistorySession(
                        workoutId = workout.id,
                        date = workout.date,
                        exercise = exercise
                    )
                }
            }

            val muscleGroup = history.firstOrNull()?.exercise?.muscleGroup ?: ""

            // Weekly Stats
            val now = ZonedDateTime.now(ZoneId.systemDefault())
            val startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0).withNano(0)
                .toInstant().toEpochMilli()
            
            val thisWeekSessions = history.filter { it.date >= startOfWeek }
            val weeklyStats = WeeklyStats(
                sessionsThisWeek = thisWeekSessions.size,
                totalVolumeThisWeek = thisWeekSessions.sumOf { session ->
                    session.exercise.sets.sumOf { (it.weight ?: 0f).toDouble() * it.reps }.toDouble()
                }.toFloat(),
                maxWeightThisWeek = thisWeekSessions.flatMap { it.exercise.sets }
                    .mapNotNull { it.weight }.maxOrNull() ?: 0f
            )

            // Trends
            val current1RM = history.firstOrNull()?.exercise?.sets?.map { 
                Calculations.calculate1RM(it.weight ?: 0f, it.reps)
            }?.maxOrNull() ?: 0f
            
            val previous1RM = history.getOrNull(1)?.exercise?.sets?.map {
                Calculations.calculate1RM(it.weight ?: 0f, it.reps)
            }?.maxOrNull() ?: 0f

            val trendDirection = when {
                current1RM > previous1RM -> TrendDirection.IMPROVING
                current1RM < previous1RM -> TrendDirection.DECLINING
                else -> TrendDirection.STABLE
            }

            val lastVolume = history.firstOrNull()?.exercise?.sets?.sumOf { 
                (it.weight ?: 0f).toDouble() * it.reps 
            }?.toFloat() ?: 0f
            
            val previousVolume = history.getOrNull(1)?.exercise?.sets?.sumOf {
                (it.weight ?: 0f).toDouble() * it.reps
            }?.toFloat() ?: 0f

            val lastVolumeTrend = when {
                lastVolume > previousVolume -> TrendDirection.IMPROVING
                lastVolume < previousVolume -> TrendDirection.DECLINING
                else -> TrendDirection.STABLE
            }

            ExerciseDetails(
                exerciseName = exerciseName,
                muscleGroup = muscleGroup,
                weeklyStats = weeklyStats,
                trends = ExerciseTrends(
                    current1RM = current1RM,
                    trendDirection = trendDirection,
                    lastVolume = lastVolume,
                    lastVolumeTrend = lastVolumeTrend
                ),
                history = history
            )
        }
    }
}
