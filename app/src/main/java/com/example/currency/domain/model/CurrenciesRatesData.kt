package com.example.currency.domain.model


/**
 * Represent currency rate model
 */
data class CurrenciesRatesData(
    val currencyCode: String,
    val currencyRate: Double,
    val date: String,
    val timestamp: Long
) {
    /** To return code when user select it from drop down **/
    override fun toString(): String = currencyCode
}