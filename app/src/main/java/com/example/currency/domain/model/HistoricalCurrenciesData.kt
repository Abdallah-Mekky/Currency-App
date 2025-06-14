package com.example.currency.domain.model


data class HistoricalCurrenciesData(
    val id: Int,
    val fromCurrencyToCurrency: String,
    val fromCurrencyAmount: String,
    val toCurrencyAmount: String
)
