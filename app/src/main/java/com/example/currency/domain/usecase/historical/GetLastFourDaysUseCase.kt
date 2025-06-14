package com.example.currency.domain.usecase.historical

import com.example.currency.data.source.local.dao.HistoricalCurrenciesDao
import com.example.currency.domain.model.DayValidator
import com.example.currency.domain.repository.HistoricalCurrenciesRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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