package com.scribblefit.feature.ledger.data.di

import com.scribblefit.feature.ledger.domain.usecase.GetWorkoutsInRangeUseCase
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LedgerDataModule {

    @Provides
    @Singleton
    fun provideGetWorkoutsInRangeUseCase(
        workoutRepository: WorkoutRepository
    ): GetWorkoutsInRangeUseCase {
        return GetWorkoutsInRangeUseCase(workoutRepository)
    }
}
