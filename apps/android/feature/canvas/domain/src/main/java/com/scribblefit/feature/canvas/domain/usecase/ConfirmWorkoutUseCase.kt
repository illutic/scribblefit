package com.scribblefit.feature.canvas.domain.usecase

import com.scribblefit.feature.canvas.domain.model.FeedItem
import com.scribblefit.feature.canvas.domain.repository.CanvasRepository
import com.scribblefit.feature.canvas.domain.repository.WorkoutSessionRepository
import com.scribblefit.feature.ledger.domain.model.ExerciseHistory
import com.scribblefit.feature.ledger.domain.model.SetHistory
import com.scribblefit.feature.ledger.domain.model.WorkoutHistory
import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val IsoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

class ConfirmWorkoutUseCase(
    private val canvasRepository: CanvasRepository,
    private val sessionRepository: WorkoutSessionRepository,
    private val ledgerRepository: LedgerRepository
) {
    suspend fun execute(confirmation: FeedItem.Confirmation) {
        val workout = confirmation.workout
        val dateEpoch = runCatching {
            LocalDate.parse(workout.date, IsoFormatter)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.getOrDefault(System.currentTimeMillis())

        val totalVolume = workout.exercises.sumOf { ex ->
            ex.sets.sumOf { s -> s.weight * s.reps }
        }

        val history = WorkoutHistory(
            id = confirmation.id,
            date = dateEpoch,
            location = workout.location,
            totalVolume = totalVolume,
            exercises = workout.exercises.map { ex ->
                ExerciseHistory(
                    canonicalName = ex.canonicalName,
                    sets = ex.sets.map { s ->
                        SetHistory(weight = s.weight, reps = s.reps, rpe = s.rpe, notes = s.notes)
                    }
                )
            }
        )
        ledgerRepository.logWorkout(history)
        sessionRepository.clearActiveSession()
        canvasRepository.removeFeedItem(confirmation.id)
    }
}
