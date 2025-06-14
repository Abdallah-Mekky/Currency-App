package com.example.currency.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currency.domain.model.CurrenciesRatesData
import com.example.currency.domain.model.CurrencyCalculation
import com.example.currency.domain.usecase.CalculateConvertedAmountUseCase
import com.example.currency.domain.usecase.GetAllCurrenciesRatesUseCase
import com.example.currency.domain.usecase.GetCurrencyRateUseCase
import com.example.currency.domain.usecase.GetLastUpdatedTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllCurrenciesRatesUseCase: GetAllCurrenciesRatesUseCase,
    private val getLastUpdatedTimeUseCase: GetLastUpdatedTimeUseCase,
    private val getCurrencyRateUseCase: GetCurrencyRateUseCase,
    private val calculateConvertedAmountUseCase: CalculateConvertedAmountUseCase
) : ViewModel() {


    private val _currenciesRatesState = MutableStateFlow<List<CurrenciesRatesData>?>(null)
    val currenciesRatesState: StateFlow<List<CurrenciesRatesData>?> = _currenciesRatesState

    private val _lastUpdateTimeState = MutableStateFlow<String?>(null)
    val lastUpdateTimeState: StateFlow<String?> = _lastUpdateTimeState

    private val _currencyCalculationState = MutableStateFlow(CurrencyCalculation())
    val currencyCalculationState: StateFlow<CurrencyCalculation> = _currencyCalculationState



    init {
        viewModelScope.launch(Dispatchers.IO) {
            val lastUpdateTime = getLastUpdatedTimeUseCase.invoke()
            _lastUpdateTimeState.emit(lastUpdateTime)


        }
    }



    fun getAllCurrenciesRates() {
        viewModelScope.launch(Dispatchers.IO){
            getAllCurrenciesRatesUseCase.invoke().collectLatest { currenciesRatesList ->
                Log.e("Api" , "HomeViewModel :: currenciesRatesList = ${
                    currenciesRatesList.joinToString()
                } ")
                _currenciesRatesState.emit(currenciesRatesList)
            }
        }
    }

    fun updateFromCurrency(fromCurrencyCode : String,fromCurrencyRate: Double) {
        viewModelScope.launch(Dispatchers.IO) {
//            val fromCurrencyRate = getCurrencyRateUseCase.invoke(fromCurrencyCode)
            _currencyCalculationState.update { currencyCalculation -> currencyCalculation.copy(
                fromCurrencyCode = fromCurrencyCode,
                fromCurrencyRate = fromCurrencyRate
            ) }

            calculateConvertedCurrency()
        }
    }

    fun updateToCurrency(toCurrencyCode: String, toCurrencyRate: Double) {
        viewModelScope.launch(Dispatchers.IO) {
//            val toCurrencyRate = getCurrencyRateUseCase.invoke(toCurrencyCode)
            _currencyCalculationState.update { currencyCalculation -> currencyCalculation.copy(
                toCurrencyCode = toCurrencyCode,
                toCurrencyRate = toCurrencyRate
            ) }

            calculateConvertedCurrency()
        }
    }

    fun updateFromCurrencyAmount(fromCurrencyAmount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _currencyCalculationState.update { currencyCalculation -> currencyCalculation.copy(
                fromCurrencyAmount = fromCurrencyAmount
            ) }

            calculateConvertedCurrency()
        }
    }

    fun updateCurrencyCalculation(fromCurrencyCode: String,toCurrencyCode: String,fromCurrencyAmount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val fromCurrencyRate = getCurrencyRateUseCase.invoke(fromCurrencyCode)
            val toCurrencyRate = getCurrencyRateUseCase.invoke(toCurrencyCode)
            _currencyCalculationState.update { currencyCalculation -> currencyCalculation.copy(
                fromCurrencyCode = fromCurrencyCode,
                fromCurrencyRate = fromCurrencyRate,
                toCurrencyCode = toCurrencyCode,
                toCurrencyRate = toCurrencyRate,
                fromCurrencyAmount = fromCurrencyAmount
            ) }

            calculateConvertedCurrency()
        }
    }

    private fun calculateConvertedCurrency() {
        viewModelScope.launch(Dispatchers.IO) {
            val toCurrencyAmount = calculateConvertedAmountUseCase.invoke(_currencyCalculationState.value)
            _currencyCalculationState.update { currencyCalculation ->
                currencyCalculation.copy(
                    toCurrencyAmount = toCurrencyAmount
                )
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        Log.e("Api" , "HomeVIEWModel :: onClear ")

    }
}