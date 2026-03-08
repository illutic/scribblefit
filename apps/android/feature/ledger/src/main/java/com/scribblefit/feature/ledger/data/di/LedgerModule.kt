package com.scribblefit.feature.ledger.data.di

import com.scribblefit.feature.ledger.data.repository.LedgerRepositoryImpl
import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LedgerModule {
    @Binds @Singleton
    abstract fun bindLedgerRepository(impl: LedgerRepositoryImpl): LedgerRepository
}
