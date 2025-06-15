package com.example.currency.di.historical

import com.example.currency.data.repository.HistoricalCurrenciesRepoImpl
import com.example.currency.data.source.local.dao.HistoricalCurrenciesDao
import com.example.currency.domain.repository.HistoricalCurrenciesRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HistoricalCurrenciesModule {

    @Provides
    @Singleton
    fun provideHistoricalCurrenciesRepo(
        historicalCurrenciesDao: HistoricalCurrenciesDao
    ): HistoricalCurrenciesRepo {
        return HistoricalCurrenciesRepoImpl(historicalCurrenciesDao)
    }
}