package com.example.currency.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currency.data.model.local.CurrenciesDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrenciesRatesDao {

    /** Insert currencies rates **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCurrenciesRates(rates: List<CurrenciesDataEntity>)

    /** Retrieve currencies rates **/
    @Query("SELECT * FROM currency_rates")
    fun getAllCurrenciesRates(): Flow<List<CurrenciesDataEntity>>

    /** Get specific rate for specific currency code **/
    @Query("SELECT currency_rate FROM currency_rates WHERE currency_code = :currencyCode LIMIT 1")
    suspend fun getRateFor(currencyCode: String): Double?
}