package com.example.currency.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currency.data.model.local.HistoricalCurrenciesDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoricalCurrenciesDao {

    /** Get last four days of user transactions **/
    @Query("""
    SELECT date FROM (
        SELECT DISTINCT date 
        FROM historical_currencies_data 
        ORDER BY date DESC 
        LIMIT 4
    ) 
    ORDER BY date ASC
    """)
    fun getLastFourDays(): Flow<List<String>>

    /** Insert user transaction / history currency **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryCurrency(transaction: HistoricalCurrenciesDataEntity)

    /** Get user transactions in specific day **/
    @Query("SELECT * FROM historical_currencies_data WHERE date = :day")
    fun getHistoricalCurrenciesDataByDay(day: String): Flow<List<HistoricalCurrenciesDataEntity>>

    /** Delete all days after for days as per not needed **/
    @Query("DELETE FROM historical_currencies_data WHERE date < :lastDay")
    suspend fun deleteDaysAfterFour(lastDay: String)
}