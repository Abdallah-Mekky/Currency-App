package com.example.currency.domain.repository

import com.example.currency.domain.model.HistoricalCurrenciesData
import kotlinx.coroutines.flow.Flow

interface HistoricalCurrenciesRepo {

    suspend fun getLastFourDays() : Flow<List<String>>

    suspend fun insertHistoryCurrency(fromCurrencyToCurrency:String,fromCurrencyAmount:String,toCurrencyAmount: String,date : String)

    suspend fun getHistoricalCurrenciesDataByDay(day : String) : Flow<List<HistoricalCurrenciesData>>

    suspend fun deleteDaysAfterFour(lastDay : String)
}