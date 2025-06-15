package com.example.currency.domain.usecase.currencies

import com.example.currency.domain.repository.CurrenciesRepo
import javax.inject.Inject

/**
 * Use case class responsible for triggering the loading of all currency exchange rates.
 *
 * This typically involves fetching the latest rates from a remote source and storing them
 * locally via the [CurrenciesRepo].
 *
 * @property currenciesRepo The repository handling data operations for currency exchange rates.
 */
class LoadAllCurrenciesRatesUseCase @Inject constructor(private val currenciesRepo: CurrenciesRepo) {
    suspend operator fun invoke() = currenciesRepo.loadAllCurrenciesRates()
}