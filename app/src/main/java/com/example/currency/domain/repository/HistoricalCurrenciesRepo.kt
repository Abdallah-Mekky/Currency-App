package com.example.currency.domain.repository

import com.example.currency.domain.model.HistoricalCurrenciesData
import kotlinx.coroutines.flow.Flow

interface HistoricalCurrenciesRepo {

    /** Get last four days **/
    suspend fun getLastFourDays(): Flow<List<String>>

    /** Insert user transaction **/
    suspend fun insertHistoryCurrency(
        fromCurrencyToCurrency: String,
        fromCurrencyAmount: String,
        toCurrencyAmount: String,
        date: String
    )

    /** Get user transactions for specific day **/
    suspend fun getHistoricalCurrenciesDataByDay(day: String): Flow<List<HistoricalCurrenciesData>>

    /** Delete all days after four days as per not needed **/
    suspend fun deleteDaysAfterFour(lastDay: String)
}