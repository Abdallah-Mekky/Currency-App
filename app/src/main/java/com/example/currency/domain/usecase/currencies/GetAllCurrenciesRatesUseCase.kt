package com.example.currency.domain.usecase.currencies

import com.example.currency.domain.repository.CurrenciesRepo
import javax.inject.Inject

class GetAllCurrenciesRatesUseCase @Inject constructor(private val currenciesRepo: CurrenciesRepo) {
    suspend operator fun invoke() =  currenciesRepo.getAllCurrenciesRates()
}