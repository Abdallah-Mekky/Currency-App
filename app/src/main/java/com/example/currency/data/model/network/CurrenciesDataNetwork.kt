package com.example.currency.data.model.network

import com.example.currency.domain.model.CurrenciesRatesData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrenciesDataNetwork(
    val success: Boolean? = null,
    val error: ErrorDetail? = null,
    val timestamp: Long? = null,
    @SerialName("base") val baseCurrency: String ? = null,
    val date: String? = null,
    val rates: Map<String, Double> ? = null
)

@Serializable
data class ErrorDetail(
    val code: Int? = null,
    val type: String? = null,
    val info: String? = null
)