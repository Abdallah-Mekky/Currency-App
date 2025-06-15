package com.example.currency.data.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.currency.domain.model.CurrenciesRatesData

@Entity(tableName = "currency_rates")
data class CurrenciesDataEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "currency_code")
    val currencyCode: String,
    @ColumnInfo(name = "currency_rate")
    val currencyRate: Double,
    val date: String,
    val timestamp: Long
) {
    /** To convert entity model to domain model [CurrenciesRatesData] **/
    fun toDomain() = CurrenciesRatesData(
        currencyCode = currencyCode,
        currencyRate = currencyRate,
        date = date,
        timestamp = timestamp
    )
}