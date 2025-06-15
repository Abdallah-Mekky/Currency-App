package com.example.currency.domain.usecase.historical

import com.example.currency.domain.model.DayValidator
import com.example.currency.domain.repository.HistoricalCurrenciesRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case class responsible for retrieving the last four days of historical currency data,
 * and mapping each day into a [DayValidator] object to support UI.
 *
 * The resulting list marks whether a day has a previous or next sibling day in the sequence,
 * enabling directional navigation (e.g., previous/next day buttons).
 *
 * @property historicalCurrenciesRepo Repository interface for accessing historical currency records.
 */
class GetLastFourDaysUseCase @Inject constructor(private val historicalCurrenciesRepo: HistoricalCurrenciesRepo) {
    suspend operator fun invoke(): Flow<List<DayValidator>> {
        val lastFourDays = historicalCurrenciesRepo.getLastFourDays()

        return lastFourDays.map { lastFourDaysList ->
            lastFourDaysList.mapIndexed { index, day ->
                DayValidator(
                    prev = index != 0,
                    currentDay = day,
                    after = index != lastFourDaysList.lastIndex
                )
            }
        }
    }
}