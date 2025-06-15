package com.example.currency.domain.usecase.currencies

import com.example.currency.domain.repository.CurrenciesRepo
import javax.inject.Inject

/**
 * Use case class responsible for retrieving all currency exchange rates.
 *
 * @property currenciesRepo The repository that provides access to currency rates data.
 */
class GetAllCurrenciesRatesUseCase @Inject constructor(private val currenciesRepo: CurrenciesRepo) {
    suspend operator fun invoke() = currenciesRepo.getAllCurrenciesRates()
}