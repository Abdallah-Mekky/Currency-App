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
import okio.IOException
import java.net.SocketException
import java.net.UnknownHostException
import com.example.currency.core.exceptions.Result
import javax.inject.Inject

class CurrenciesRepoImpl @Inject constructor(private val webService: WebService,
    private val currenciesRatesDao: CurrenciesRatesDao,
    private val sharedPrefs: SharedPrefs) : CurrenciesRepo {
    override suspend fun loadAllCurrenciesRates() : Result<Unit> {
        try {
            val lastUpdateTime = sharedPrefs.getLastUpdate()
            val now = System.currentTimeMillis()
            val twentyFourHours = 24 * 60 * 60 * 1000L

            if (lastUpdateTime == 0L || (now - lastUpdateTime > twentyFourHours)) {
                Log.e("Api", "CurrenciesRepoImpl inside if")

                val response = webService.getAllCurrenciesByBase()

                // Check API success field INSIDE try block, not in catch
                if (response.success == true) {
                    Log.e("Api", "CurrenciesRepoImpl inside true")

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
                    sharedPrefs.setLastUpdate(System.currentTimeMillis())

                    return Result.Success(Unit)

                } else {
                    // API returned success: false - this is NOT an exception
                    Log.e("Api", "CurrenciesRepoImpl API returned error: ${response.error?.info}")
                    return Result.ApiException(message = response.error?.info ?: "API returned an error")
                }
            }

            // If we don't need to update (within 24 hours), return success
            return Result.Success(Unit)

        } catch (exception: Exception) {
            Log.e("Api", "Exception occurred: ${exception.message}", exception)

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
            currenciesRatesEntityList.map { currencyRateEntityItem ->  currencyRateEntityItem.toDomain() }
        }
    }
}