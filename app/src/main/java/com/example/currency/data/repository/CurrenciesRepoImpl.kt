package com.example.currency.data.repository

import com.example.currency.data.model.local.CurrenciesDataEntity
import com.example.currency.data.source.local.dao.CurrenciesRatesDao
import com.example.currency.data.source.local.preferences.DataStores
import com.example.currency.data.source.network.WebService
import com.example.currency.domain.model.CurrenciesRatesData
import com.example.currency.domain.repository.CurrenciesRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.IOException
import java.net.SocketException
import java.net.UnknownHostException
import com.example.currency.core.exceptions.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CurrenciesRepoImpl @Inject constructor(
    private val webService: WebService,
    private val currenciesRatesDao: CurrenciesRatesDao,
    private val dataStores: DataStores
) : CurrenciesRepo {

    override suspend fun loadAllCurrenciesRates(): Result<Unit> {
        try {
            val lastUpdateTime = dataStores.lastUpdateTime.first()
            val now = System.currentTimeMillis()
            val twentyFourHours = 24 * 60 * 60 * 1000L

            if (lastUpdateTime == 0L || (now - lastUpdateTime > twentyFourHours)) {

                val response = webService.getAllCurrenciesByBase()

                if (response.success == true) {

                    val ratesList = response.rates?.map {
                        CurrenciesDataEntity(
                            currencyCode = it.key,
                            currencyRate = it.value,
                            date = response.date ?: "",
                            timestamp = response.timestamp ?: 0L
                        )
                    }

                    ratesList?.let { rates ->
                        currenciesRatesDao.insertAllCurrenciesRates(rates)
                    }
                    dataStores.setLastUpdateTime(System.currentTimeMillis())
                    return Result.Success(Unit)

                } else {
                    return Result.ApiException(
                        message = response.error?.info ?: "API returned an error"
                    )
                }
            }

            // If we don't need to update (within 24 hours), return success
            return Result.Success(Unit)

        } catch (exception: Exception) {

            //Handle all exceptions
            return when (exception) {
                is UnknownHostException -> {
                    Result.NetworkException("No internet connection. Please check your network and try again.")
                }

                is SocketException -> {
                    Result.NetworkException("Network connection error. Please try again.")
                }

                is IOException -> {
                    Result.NetworkException("Network error. Please check your connection and try again.")
                }

                else -> {
                    Result.UnexpectedException("Unexpected error occurred: ${exception.message}")
                }
            }
        }
    }

    override suspend fun getAllCurrenciesRates(): Flow<List<CurrenciesRatesData>> {
        return currenciesRatesDao.getAllCurrenciesRates().map { currenciesRatesEntityList ->
            currenciesRatesEntityList.map { currencyRateEntityItem -> currencyRateEntityItem.toDomain() }
        }
    }
}