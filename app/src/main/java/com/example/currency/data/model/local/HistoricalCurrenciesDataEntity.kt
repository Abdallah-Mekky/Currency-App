package com.example.currency.data.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.currency.domain.model.HistoricalCurrenciesData

@Entity(tableName = "historical_currencies_data")
data class HistoricalCurrenciesDataEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Int = 0,
    @ColumnInfo
    val date: String,
    @ColumnInfo(name = "from_currency_to_Currency")
    val fromCurrencyToCurrency: String,
    @ColumnInfo(name = "from_amount")
    val fromCurrencyAmount: String,
    @ColumnInfo(name = "to_amount")
    val toCurrencyAmount: String
) {
    /** To convert entity model to domain model [HistoricalCurrenciesData] **/
    fun toDomain() = HistoricalCurrenciesData(
        id = id,
        fromCurrencyToCurrency = fromCurrencyToCurrency,
        fromCurrencyAmount = fromCurrencyAmount,
        toCurrencyAmount = toCurrencyAmount
    )
}