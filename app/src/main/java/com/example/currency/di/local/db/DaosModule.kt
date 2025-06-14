package com.example.currency.di.local.db

import com.example.currency.data.source.local.CurrenciesDatabase
import com.example.currency.data.source.local.dao.CurrenciesRatesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun providesCurrenciesDao(database: CurrenciesDatabase): CurrenciesRatesDao =
        database.currenciesRatesDao()
}
