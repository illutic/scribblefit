package com.scribblefit.feature.ledger.domain.usecase

import com.scribblefit.core.model.Workout
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class GetWorkoutsInRangeUseCase(
    private val workoutRepository: WorkoutRepository
) {
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<List<Workout>> {
        val startMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return workoutRepository.getWorkoutsInRange(startMillis, endMillis).map { workouts ->
            workouts.filter { it.exercises.isNotEmpty() }
        }
    }
}
