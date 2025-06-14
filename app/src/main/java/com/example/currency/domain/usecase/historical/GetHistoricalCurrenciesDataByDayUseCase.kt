package com.example.currency.domain.usecase.historical

import com.example.currency.data.source.local.dao.HistoricalCurrenciesDao
import com.example.currency.domain.repository.HistoricalCurrenciesRepo
import javax.inject.Inject

class GetHistoricalCurrenciesDataByDayUseCase @Inject constructor(private val historicalCurrenciesRepo: HistoricalCurrenciesRepo) {
    suspend operator fun invoke(day: String) = historicalCurrenciesRepo.getHistoricalCurrenciesDataByDay(day = day)
}