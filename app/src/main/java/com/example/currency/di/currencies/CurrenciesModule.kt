package com.example.currency.di.currencies

import com.example.currency.data.repository.CurrenciesRepoImpl
import com.example.currency.data.source.local.dao.CurrenciesRatesDao
import com.example.currency.data.source.local.preferences.DataStores
import com.example.currency.data.source.network.WebService
import com.example.currency.domain.repository.CurrenciesRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CurrenciesModule {

    @Provides
    @Singleton
    fun provideCurrenciesRepo(
        webService: WebService,
        currenciesRatesDao: CurrenciesRatesDao,
        dataStores: DataStores
    ): CurrenciesRepo {
        return CurrenciesRepoImpl(webService, currenciesRatesDao, dataStores)
    }
}