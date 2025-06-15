package com.example.currency.domain.usecase.historical

import com.example.currency.domain.repository.HistoricalCurrenciesRepo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Use case class responsible for saving a currency conversion transaction
 * to the historical transactions repository.
 *
 * It records the transaction details along with the current date, formatted
 * in "dd-MM-yyyy" format.
 *
 * @property historicalCurrenciesRepo Repository interface for handling currency transaction history operations.
 */
class AddCurrencyTransactionUseCase @Inject constructor(private val historicalCurrenciesRepo: HistoricalCurrenciesRepo) {

    suspend operator fun invoke(
        fromCurrencyToCurrency: String, fromCurrencyAmount: String,
        toCurrencyAmount: String
    ) {

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