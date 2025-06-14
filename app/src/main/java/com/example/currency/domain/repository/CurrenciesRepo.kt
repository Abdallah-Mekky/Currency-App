package com.example.currency.domain.repository

import com.example.currency.domain.model.CurrenciesRatesData
import kotlinx.coroutines.flow.Flow

interface CurrenciesRepo {

    suspend fun loadAllCurrenciesRates()

    suspend fun getAllCurrenciesRates() : Flow<List<CurrenciesRatesData>>
}