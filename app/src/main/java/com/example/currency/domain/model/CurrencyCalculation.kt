package com.example.currency.domain.model

data class CurrencyCalculation(
    val fromCurrencyCode: String? = null,
    val fromCurrencyRate: Double? = null,
    val toCurrencyCode: String? = null,
    val toCurrencyRate: Double? = null,
    val fromCurrencyAmount: Int? = null,
    val toCurrencyAmount: Double? = null
)
