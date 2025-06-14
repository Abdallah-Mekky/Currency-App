package com.example.currency.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currency.data.model.local.CurrenciesDataEntity
import com.example.currency.data.model.local.HistoricalCurrenciesDataEntity
import com.example.currency.data.source.local.dao.CurrenciesRatesDao
import com.example.currency.data.source.local.dao.HistoricalCurrenciesDao

@Database(entities = [CurrenciesDataEntity::class,HistoricalCurrenciesDataEntity::class], version = 2, exportSchema = false)
abstract class CurrenciesDatabase : RoomDatabase() {
    abstract fun currenciesRatesDao() : CurrenciesRatesDao
    abstract fun historicalCurrenciesDao() : HistoricalCurrenciesDao
}