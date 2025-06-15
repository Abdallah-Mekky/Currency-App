package com.example.currency.data.source.network

import com.example.currency.data.model.network.CurrenciesDataNetwork
import com.example.currencytask.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Represent all end-points
 */
interface WebService {

    /** Get all currencies by currency base like `USD` **/
    @GET("latest")
    suspend fun getAllCurrenciesByBase(
        @Query("access_key") apiKey: String = BuildConfig.FIXER_API_KEY
    ): CurrenciesDataNetwork
}