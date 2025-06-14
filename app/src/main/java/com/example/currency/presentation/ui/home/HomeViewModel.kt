package com.example.currency.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currency.domain.model.CurrenciesRatesData
import com.example.currency.domain.model.CurrencyCalculation
import com.example.currency.domain.usecase.currencies.CalculateConvertedAmountUseCase
import com.example.currency.domain.usecase.currencies.GetAllCurrenciesRatesUseCase
import com.example.currency.domain.usecase.currencies.GetCurrencyRateUseCase
import com.example.currency.domain.usecase.currencies.GetFormattedLastUpdatedTimeUseCase
import com.example.currency.domain.usecase.currencies.GetLastUpdatedTimeUseCase
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
    private val getLastUpdatedTimeUseCase: GetLastUpdatedTimeUseCase,
    private val getCurrencyRateUseCase: GetCurrencyRateUseCase,
    private val calculateConvertedAmountUseCase: CalculateConvertedAmountUseCase,
    private val addCurrencyTransactionUseCase: AddCurrencyTransactionUseCase
) : ViewModel() {


    private val _currenciesRatesState = MutableStateFlow<List<CurrenciesRatesData>?>(null)
    val currenciesRatesState: StateFlow<List<CurrenciesRatesData>?> = _currenciesRatesState

    private val _lastUpdateTimeState = MutableStateFlow<String?>(null)
    val lastUpdateTimeState: StateFlow<String?> = _lastUpdateTimeState

    private val _currencyCalculationState = MutableStateFlow(CurrencyCalculation())
    val currencyCalculationState: StateFlow<CurrencyCalculation> = _currencyCalculationState

    private val _uiErrors = MutableSharedFlow<String>()
    val uiErrors: SharedFlow<String> = _uiErrors.asSharedFlow()


    init {
        viewModelScope.launch(Dispatchers.IO) {


        }
    }


    fun getLastUpdateTime() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lastUpdateTime = getFormattedLastUpdatedTimeUseCase.invoke()
                _lastUpdateTimeState.emit(lastUpdateTime)
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }
        }
    }

    fun getAllCurrenciesRates() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAllCurrenciesRatesUseCase.invoke().collectLatest { currenciesRatesList ->
                    Log.e(
                        "Api", "HomeViewModel :: currenciesRatesList = ${
                            currenciesRatesList.joinToString()
                        } "
                    )
                    _currenciesRatesState.emit(currenciesRatesList)
                }
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }

        }
    }

    fun updateFromCurrency(fromCurrencyCode: String, fromCurrencyRate: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.e("Api", "HomeViewModel :: updateFromCurrency 1 =")
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

    fun updateToCurrency(toCurrencyCode: String, toCurrencyRate: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("Api", "HomeViewModel :: updateFromCurrency 2 =")
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
//            val toCurrencyRate = getCurrencyRateUseCase.invoke(toCurrencyCode)

        }
    }

    fun updateFromCurrencyAmount(fromCurrencyAmount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("Api", "HomeViewModel :: updateFromCurrency 3 =")
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

    fun updateCurrencyCalculation(
        fromCurrencyCode: String,
        toCurrencyCode: String,
        fromCurrencyAmount: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("Api", "HomeViewModel :: updateFromCurrency 4 =")
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

    private suspend fun insertCurrencyTransaction() {
        try {
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


    override fun onCleared() {
        super.onCleared()
        Log.e("Api", "HomeVIEWModel :: onClear ")

    }
}