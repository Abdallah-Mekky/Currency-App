package com.example.currency.domain.usecase.historical

import com.example.currency.domain.repository.HistoricalCurrenciesRepo
import javax.inject.Inject

/**
 * Use case class responsible for retrieving historical currency conversion data for a specific day.
 *
 * This allows the application to display or analyze past conversion transactions based on the given date.
 *
 * @property historicalCurrenciesRepo Repository interface for accessing historical currency transaction data.
 */
class GetHistoricalCurrenciesDataByDayUseCase @Inject constructor(private val historicalCurrenciesRepo: HistoricalCurrenciesRepo) {
    suspend operator fun invoke(day: String) =
        historicalCurrenciesRepo.getHistoricalCurrenciesDataByDay(day = day)
}