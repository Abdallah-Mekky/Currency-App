package com.example.currency.domain.repository

import com.example.currency.core.exceptions.Result
import com.example.currency.domain.model.CurrenciesRatesData
import kotlinx.coroutines.flow.Flow

interface CurrenciesRepo {

    suspend fun loadAllCurrenciesRates() : Result<Unit>

    suspend fun getAllCurrenciesRates() : Flow<List<CurrenciesRatesData>>
}