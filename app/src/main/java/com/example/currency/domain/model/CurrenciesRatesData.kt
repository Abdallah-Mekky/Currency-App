package com.example.currency.domain.model



data class CurrenciesRatesData(
    val currencyCode: String,
    val currencyRate: Double,
    val date: String,
    val timestamp: Long
) {
    override fun toString(): String = currencyCode
}