package com.example.currency.domain.usecase.historical

import com.example.currency.domain.repository.HistoricalCurrenciesRepo
import javax.inject.Inject

class DeleteDaysAfterFourUseCase  @Inject constructor(private val historicalCurrenciesRepo: HistoricalCurrenciesRepo) {
    suspend operator fun invoke(day: String) = historicalCurrenciesRepo.deleteDaysAfterFour(lastDay = day)
}