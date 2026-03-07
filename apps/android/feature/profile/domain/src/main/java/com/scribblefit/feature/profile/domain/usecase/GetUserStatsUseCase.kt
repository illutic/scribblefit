package com.scribblefit.feature.profile.domain.usecase

import com.scribblefit.feature.profile.domain.model.UserStats
import com.scribblefit.feature.profile.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserStatsUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<UserStats> {
        return repository.getUserStats()
    }
}
