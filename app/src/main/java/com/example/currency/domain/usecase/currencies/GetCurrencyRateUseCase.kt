package com.example.currency.domain.usecase.currencies

import com.example.currency.data.source.local.dao.CurrenciesRatesDao
import javax.inject.Inject

/**
 * Use case class responsible for retrieving the exchange rate for a specific currency code.
 *
 * @property currenciesRatesDao Data Access Object that provides access to stored currency rates.
 */
class GetCurrencyRateUseCase @Inject constructor(private val currenciesRatesDao: CurrenciesRatesDao) {
    suspend operator fun invoke(currencyCode: String): Double? {
        return currenciesRatesDao.getRateFor(currencyCode = currencyCode)
    }
}