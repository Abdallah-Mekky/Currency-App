package com.example.currency.data.repository

import android.util.Log
import com.example.currency.data.model.local.HistoricalCurrenciesDataEntity
import com.example.currency.data.source.local.dao.HistoricalCurrenciesDao
import com.example.currency.domain.model.HistoricalCurrenciesData
import com.example.currency.domain.repository.HistoricalCurrenciesRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoricalCurrenciesRepoImpl @Inject constructor(private val historicalCurrenciesDao: HistoricalCurrenciesDao) : HistoricalCurrenciesRepo {
    override suspend fun getLastFourDays(): Flow<List<String>> {
        return historicalCurrenciesDao.getLastFourDays()
    }

    override suspend fun insertHistoryCurrency(fromCurrencyToCurrency:String,fromCurrencyAmount:String,toCurrencyAmount: String,date : String) {
        historicalCurrenciesDao.insertHistoryCurrency(
            HistoricalCurrenciesDataEntity(
                date = date,
                fromCurrencyToCurrency = fromCurrencyToCurrency,
                fromCurrencyAmount = fromCurrencyAmount,
                toCurrencyAmount = toCurrencyAmount
            )
        )
    }

    override suspend fun getHistoricalCurrenciesDataByDay(day: String): Flow<List<HistoricalCurrenciesData>> {
        Log.e("Api" , "HistoricalCurrenciesRepoImpl :: ,day = ${day} ,")
        return historicalCurrenciesDao.getHistoricalCurrenciesDataByDay(day = day).map { historicalCurrenciesList ->
            historicalCurrenciesList.map { historicalCurrencyItem ->
                historicalCurrencyItem.toDomain()
            }
        }
    }

    override suspend fun deleteDaysAfterFour(lastDay: String) {
        historicalCurrenciesDao.deleteDaysAfterFour(lastDay)
    }
}