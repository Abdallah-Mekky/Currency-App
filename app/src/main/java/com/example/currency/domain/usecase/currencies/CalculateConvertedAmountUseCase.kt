package com.example.currency.domain.usecase.currencies

import com.example.currency.domain.model.CurrencyCalculation
import javax.inject.Inject

/**
 * Use case class responsible for calculating the converted amount between two currencies.
 *
 * This class multiplies the source currency amount by the ratio of the target currency rate
 * to the source currency rate to compute the equivalent amount in the target currency.
 */
class CalculateConvertedAmountUseCase @Inject constructor() {
    suspend operator fun invoke(currencyCalculation: CurrencyCalculation): Double {
        return currencyCalculation.fromCurrencyAmount!! *
                (currencyCalculation.toCurrencyRate!! / currencyCalculation.fromCurrencyRate!!)
    }
}