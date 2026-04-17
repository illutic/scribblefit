package com.scribblefit.feature.ledger.domain.usecase

import com.scribblefit.core.model.Workout
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneOffset

class GetWorkoutsInRangeUseCase(
    private val workoutRepository: WorkoutRepository
) {
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<List<Workout>> {
        val startMillis = startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val endMillis = endDate.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli()
        return workoutRepository.getWorkoutsInRange(startMillis, endMillis).map { workouts ->
            workouts.filter { it.exercises.isNotEmpty() }
        }
    }
}
