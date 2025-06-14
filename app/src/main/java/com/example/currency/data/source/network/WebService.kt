package com.example.currency.data.source.network

import com.example.currency.data.model.network.CurrenciesDataNetwork
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Represent all end-points
 */
interface WebService {

    /** Get all currencies by currency base like `EUR` **/
    @GET("latest")
    suspend fun getAllCurrenciesByBase(
        @Query("access_key") apiKey : String = "3777ea8adad5ff5955879bbeeef51ed4"): CurrenciesDataNetwork
}