package com.example.currency.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currency.domain.model.CurrenciesRatesData
import com.example.currency.domain.model.CurrencyCalculation
import com.example.currency.domain.usecase.currencies.CalculateConvertedAmountUseCase
import com.example.currency.domain.usecase.currencies.GetAllCurrenciesRatesUseCase
import com.example.currency.domain.usecase.currencies.GetCurrencyRateUseCase
import com.example.currency.domain.usecase.currencies.GetFormattedLastUpdatedTimeUseCase
import com.example.currency.domain.usecase.historical.AddCurrencyTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllCurrenciesRatesUseCase: GetAllCurrenciesRatesUseCase,
    private val getFormattedLastUpdatedTimeUseCase: GetFormattedLastUpdatedTimeUseCase,
    private val getCurrencyRateUseCase: GetCurrencyRateUseCase,
    private val calculateConvertedAmountUseCase: CalculateConvertedAmountUseCase,
    private val addCurrencyTransactionUseCase: AddCurrencyTransactionUseCase
) : ViewModel() {
    /** To track currencies rates state **/
    private val _currenciesRatesState = MutableStateFlow<List<CurrenciesRatesData>?>(null)
    val currenciesRatesState: StateFlow<List<CurrenciesRatesData>?> = _currenciesRatesState

    /** To track last update time state **/
    private val _lastUpdateTimeState = MutableStateFlow<String?>(null)
    val lastUpdateTimeState: StateFlow<String?> = _lastUpdateTimeState

    /** To track all calculations of currencies state **/
    private val _currencyCalculationState = MutableStateFlow(CurrencyCalculation())
    val currencyCalculationState: StateFlow<CurrencyCalculation> = _currencyCalculationState

    /** To control ui errors to show error one time **/
    private val _uiErrors = MutableSharedFlow<String>(replay = 1)
    val uiErrors: SharedFlow<String> = _uiErrors.asSharedFlow()

    /**
     * Retrieves the last update time using the use case and emits it to the UI state.
     */
    fun getLastUpdateTime() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lastUpdateTime = getFormattedLastUpdatedTimeUseCase.invoke()
                _lastUpdateTimeState.emit("Last update time : $lastUpdateTime")
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }
        }
    }

    /**
     * Fetches all currencies rates using the use case and emits the result to the UI state.
     */
    fun getAllCurrenciesRates() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAllCurrenciesRatesUseCase.invoke().collectLatest { currenciesRatesList ->
                    _currenciesRatesState.emit(currenciesRatesList)
                }
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }
        }
    }

    /**
     * Updates the source currency code and rate in the current currency calculation state.
     *
     * @param fromCurrencyCode The selected source currency code.
     * @param fromCurrencyRate The exchange rate of the source currency.
     */
    fun updateFromCurrency(fromCurrencyCode: String, fromCurrencyRate: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _currencyCalculationState.update { currencyCalculation ->
                    currencyCalculation.copy(
                        fromCurrencyCode = fromCurrencyCode,
                        fromCurrencyRate = fromCurrencyRate
                    )
                }

                calculateConvertedCurrency()
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }

        }
    }

    /**
     * Updates the target currency code and rate in the current currency calculation state.
     *
     * @param toCurrencyCode The selected target currency code.
     * @param toCurrencyRate The exchange rate of the target currency.
     */
    fun updateToCurrency(toCurrencyCode: String, toCurrencyRate: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _currencyCalculationState.update { currencyCalculation ->
                    currencyCalculation.copy(
                        toCurrencyCode = toCurrencyCode,
                        toCurrencyRate = toCurrencyRate
                    )
                }
                calculateConvertedCurrency()
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }
        }
    }

    /**
     * Updates the amount of the source currency in the currency calculation state
     * if the new amount is different from the current one.
     *
     * @param fromCurrencyAmount The new amount of the source currency entered by the user.
     */
    fun updateFromCurrencyAmount(fromCurrencyAmount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (_currencyCalculationState.value.fromCurrencyAmount != fromCurrencyAmount) {
                    _currencyCalculationState.update { currencyCalculation ->
                        currencyCalculation.copy(
                            fromCurrencyAmount = fromCurrencyAmount
                        )
                    }
                    calculateConvertedCurrency()
                }
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }
        }
    }

    /**
     * Updates the full currency calculation state with the selected currencies and amount.
     *
     * @param fromCurrencyCode The code of the source currency.
     * @param toCurrencyCode The code of the target currency.
     * @param fromCurrencyAmount The amount to convert from the source currency.
     */
    fun updateCurrencyCalculation(
        fromCurrencyCode: String,
        toCurrencyCode: String,
        fromCurrencyAmount: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fromCurrencyRate = getCurrencyRateUseCase.invoke(fromCurrencyCode)
                val toCurrencyRate = getCurrencyRateUseCase.invoke(toCurrencyCode)
                _currencyCalculationState.update { currencyCalculation ->
                    currencyCalculation.copy(
                        fromCurrencyCode = fromCurrencyCode,
                        fromCurrencyRate = fromCurrencyRate,
                        toCurrencyCode = toCurrencyCode,
                        toCurrencyRate = toCurrencyRate,
                        fromCurrencyAmount = fromCurrencyAmount
                    )
                }
                calculateConvertedCurrency()
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }
        }
    }

    /**
     * Calculates the converted currency amount based on the current calculation state.
     */
    private fun calculateConvertedCurrency() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val toCurrencyAmount =
                    calculateConvertedAmountUseCase.invoke(_currencyCalculationState.value)
                _currencyCalculationState.update { currencyCalculation ->
                    currencyCalculation.copy(
                        toCurrencyAmount = toCurrencyAmount
                    )
                }

                insertCurrencyTransaction()
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }
        }
    }

    /**
     * Inserts a currency transaction record if the fromCurrencyAmount is greater than zero.
     */
    private suspend fun insertCurrencyTransaction() {
        try {
            //To avoid inserting row with zero values
            if (_currencyCalculationState.value.fromCurrencyAmount!! > 0) {
                addCurrencyTransactionUseCase.invoke(
                    fromCurrencyToCurrency =
                    "From ${_currencyCalculationState.value.fromCurrencyCode} To ${_currencyCalculationState.value.toCurrencyCode}",
                    fromCurrencyAmount = "${_currencyCalculationState.value.fromCurrencyAmount}",
                    toCurrencyAmount = _currencyCalculationState.value.toCurrencyAmount.toString()
                )
            }
        } catch (exception: Exception) {
            _uiErrors.emit(exception.message ?: "")
        }
    }
}