package com.example.currency.data.model.network

import com.example.currency.domain.model.CurrenciesRatesData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrenciesDataNetwork(
    val success: Boolean,
    val timestamp: Long,
    @SerialName("base") val baseCurrency: String,
    val date: String,
    val rates: Map<String, Double>
) {
//    fun toDomain() = CurrenciesRatesData(
//        status = success,
//        timestamp = timestamp,
//        date = date,
//        rates = rates
//    )
}