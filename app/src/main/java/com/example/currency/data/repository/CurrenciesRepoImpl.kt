package com.example.currency.data.repository

import android.util.Log
import com.example.currency.data.model.local.CurrenciesDataEntity
import com.example.currency.data.source.local.dao.CurrenciesRatesDao
import com.example.currency.data.source.local.preferences.SharedPrefs
import com.example.currency.data.source.network.WebService
import com.example.currency.domain.model.CurrenciesRatesData
import com.example.currency.domain.repository.CurrenciesRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CurrenciesRepoImpl @Inject constructor(private val webService: WebService,
    private val currenciesRatesDao: CurrenciesRatesDao,
    private val sharedPrefs: SharedPrefs) : CurrenciesRepo {
    override suspend fun loadAllCurrenciesRates(){

        val lastUpdateTime = sharedPrefs.getLastUpdate()
        val now = System.currentTimeMillis()
        val twentyFourHours = 24 * 60 * 60 * 1000L

        if (lastUpdateTime == 0L || (now - lastUpdateTime > twentyFourHours)) {
            Log.e("Api" , "CurrenciesRepoImpl inside if ")
            val response = webService.getAllCurrenciesByBase()
            val ratesList = response.rates.map { CurrenciesDataEntity(
                currencyCode = it.key,
                currencyRate = it.value,
                date = response.date,
                timestamp = response.timestamp
            ) }
            currenciesRatesDao.insertAllCurrenciesRates(ratesList)
            sharedPrefs.setLastUpdate(System.currentTimeMillis())
        }


//        when(lastUpdateTime) {
//            0L ,  -> {
//                Log.e("Api" , "CurrenciesRepoImpl inside if ")
//                val response = webService.getAllCurrenciesByBase()
//                val ratesList = response.rates.map { CurrenciesDataEntity(
//                    currencyCode = it.key,
//                    currencyRate = it.value,
//                    date = response.date,
//                    timestamp = response.timestamp
//                ) }
//                currenciesRatesDao.insertAllCurrenciesRates(ratesList)
//                sharedPrefs.setLastUpdate(System.currentTimeMillis())
//            }
//        }
//        val rates = currenciesRatesDao.getCurrencyRatesCount()
//        Log.e("Api" , "CurrenciesRepoImpl :: rates = ${
//            rates
//        } ")
//        if (rates == 0) {
//            Log.e("Api" , "CurrenciesRepoImpl inside if ")
//            val response = webService.getAllCurrenciesByBase()
//            val ratesList = response.rates.map { CurrenciesDataEntity(
//                currencyCode = it.key,
//                currencyRate = it.value,
//                date = response.date,
//                timestamp = response.timestamp
//            ) }
//            currenciesRatesDao.insertAllCurrenciesRates(ratesList)
//        }
    }

    override suspend fun getAllCurrenciesRates(): Flow<List<CurrenciesRatesData>> {
        return currenciesRatesDao.getAllCurrenciesRates().map { currenciesRatesEntityList ->
            currenciesRatesEntityList.map { currencyRateEntityItem ->  currencyRateEntityItem.toDomain() }
        }
    }
}