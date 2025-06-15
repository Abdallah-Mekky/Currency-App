package com.example.currency.domain.repository

import com.example.currency.core.exceptions.Result
import com.example.currency.domain.model.CurrenciesRatesData
import kotlinx.coroutines.flow.Flow

interface CurrenciesRepo {

    /** Load all currencies rates for first time or after 24 hours **/
    suspend fun loadAllCurrenciesRates(): Result<Unit>

    /** Get all currencies rates **/
    suspend fun getAllCurrenciesRates(): Flow<List<CurrenciesRatesData>>
}