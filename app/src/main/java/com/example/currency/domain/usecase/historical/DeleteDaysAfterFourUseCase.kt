package com.example.currency.domain.usecase.historical

import com.example.currency.domain.repository.HistoricalCurrenciesRepo
import javax.inject.Inject

/**
 * Use case class responsible for deleting historical currency records
 * that exceed the limit of four most recent days.
 *
 * This helps maintain a fixed-size history by removing older or excess entries
 * beyond the specified day.
 *
 * @property historicalCurrenciesRepo Repository interface for managing historical currency data.
 */
class DeleteDaysAfterFourUseCase @Inject constructor(private val historicalCurrenciesRepo: HistoricalCurrenciesRepo) {
    suspend operator fun invoke(day: String) =
        historicalCurrenciesRepo.deleteDaysAfterFour(lastDay = day)
}