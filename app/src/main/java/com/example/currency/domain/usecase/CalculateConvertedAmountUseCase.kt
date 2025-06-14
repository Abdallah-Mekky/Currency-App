package com.example.currency.domain.usecase

import com.example.currency.data.source.local.dao.CurrenciesRatesDao
import com.example.currency.domain.model.CurrencyCalculation
import javax.inject.Inject

class CalculateConvertedAmountUseCase @Inject constructor(private val currenciesRatesDao: CurrenciesRatesDao) {
    suspend operator fun invoke(currencyCalculation: CurrencyCalculation): Double {
//        val fromCurrencyRate = currenciesRatesDao.getRateFor(currencyCalculation.fromCurrencyCode ?: "")
//        val toCurrencyRate = currenciesRatesDao.getRateFor(currencyCalculation.toCurrencyCode ?: "")
        return currencyCalculation.fromCurrencyAmount!! * (currencyCalculation.toCurrencyRate!! / currencyCalculation.fromCurrencyRate!!)
    }
}