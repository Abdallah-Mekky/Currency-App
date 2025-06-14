package com.example.currency.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currency.data.model.local.CurrenciesDataEntity
import com.example.currency.data.source.local.dao.CurrenciesRatesDao

@Database(entities = [CurrenciesDataEntity::class], version = 1, exportSchema = false)
abstract class CurrenciesDatabase : RoomDatabase() {
    abstract fun currenciesRatesDao() : CurrenciesRatesDao
}