package com.scribblefit.feature.profile.data.repository

import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import com.scribblefit.feature.profile.domain.model.UserStats
import com.scribblefit.feature.profile.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val ledgerRepository: LedgerRepository
) : UserRepository {

    override fun getUserStats(): Flow<UserStats> =
        ledgerRepository.getWorkoutHistory().map { history ->
            UserStats(
                totalWorkouts = history.size,
                lifetimeVolume = history.sumOf { workout ->
                    workout.exercises.sumOf { exercise ->
                        exercise.sets.sumOf { set ->
                            set.weight * set.reps
                        }
                    }
                },
                prCount = 0,
                joinDate = history.minOfOrNull { it.date }
            )
        }
}
