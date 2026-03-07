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

    override fun getUserStats(): Flow<UserStats> {
        return ledgerRepository.getWorkoutHistory().map { history ->
            UserStats(
                totalWorkouts = history.size,
                lifetimeVolume = history.sumOf { it.totalVolume },
                prCount = 0, // Logic for PR calculation would go here
                joinDate = history.minByOrNull { it.date }?.date ?: System.currentTimeMillis()
            )
        }
    }
}
