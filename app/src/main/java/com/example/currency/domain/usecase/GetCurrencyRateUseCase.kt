package com.example.currency.domain.usecase

import com.example.currency.data.source.local.dao.CurrenciesRatesDao
import javax.inject.Inject

class GetCurrencyRateUseCase @Inject constructor(private val currenciesRatesDao: CurrenciesRatesDao) {
    suspend operator fun invoke(currencyCode : String) : Double? {
        return currenciesRatesDao.getRateFor(currencyCode = currencyCode)
    }
}