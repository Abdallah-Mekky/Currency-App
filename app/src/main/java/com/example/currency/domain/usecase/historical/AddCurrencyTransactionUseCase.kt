package com.example.currency.domain.usecase.historical

import com.example.currency.domain.repository.HistoricalCurrenciesRepo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AddCurrencyTransactionUseCase @Inject constructor(private val historicalCurrenciesRepo: HistoricalCurrenciesRepo) {

    suspend operator fun invoke(fromCurrencyToCurrency: String, fromCurrencyAmount: String,
                                toCurrencyAmount: String) {

        historicalCurrenciesRepo.insertHistoryCurrency(
            date = formatTimestamp(System.currentTimeMillis()),
            fromCurrencyToCurrency = fromCurrencyToCurrency,
            fromCurrencyAmount = fromCurrencyAmount,
            toCurrencyAmount = toCurrencyAmount
        )
    }


    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        return format.format(date)
    }
}