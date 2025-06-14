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

    private val _uiErrors = MutableSharedFlow<Unit>()
    val uiErrors: SharedFlow<Unit> = _uiErrors.asSharedFlow()



    init {
        viewModelScope.launch(Dispatchers.IO) {



        }
    }

    fun checkCurrenciesRatesNeededUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            val lastUpdatedTime = getLastUpdatedTimeUseCase.invoke()
            val now = System.currentTimeMillis()
            val twentyFourHours = 24 * 60 * 60 * 1000L

            if (now - lastUpdatedTime > twentyFourHours) {
                _uiErrors.emit(Unit)
            }
        }
    }


    fun getLastUpdateTime() {
        viewModelScope.launch(Dispatchers.IO){
            val lastUpdateTime = getFormattedLastUpdatedTimeUseCase.invoke()
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
            Log.e("Api" , "HomeViewModel :: updateFromCurrency 1 =")
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
            Log.e("Api" , "HomeViewModel :: updateFromCurrency 2 =")

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
            Log.e("Api" , "HomeViewModel :: updateFromCurrency 3 =")

            if (_currencyCalculationState.value.fromCurrencyAmount != fromCurrencyAmount) {

                _currencyCalculationState.update { currencyCalculation -> currencyCalculation.copy(
                    fromCurrencyAmount = fromCurrencyAmount
                ) }

                calculateConvertedCurrency()
            }
        }
    }

    fun updateCurrencyCalculation(fromCurrencyCode: String,toCurrencyCode: String,fromCurrencyAmount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("Api" , "HomeViewModel :: updateFromCurrency 4 =")

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
            val toCurrencyAmount =
                calculateConvertedAmountUseCase.invoke(_currencyCalculationState.value)
            _currencyCalculationState.update { currencyCalculation ->
                currencyCalculation.copy(
                    toCurrencyAmount = toCurrencyAmount
                )
            }

            insertCurrencyTransaction()
        }
    }

    private suspend fun insertCurrencyTransaction(){
        if (_currencyCalculationState.value.fromCurrencyAmount!! > 0) {
            addCurrencyTransactionUseCase.invoke(
                fromCurrencyToCurrency =
                "From ${_currencyCalculationState.value.fromCurrencyCode} To ${_currencyCalculationState.value.toCurrencyCode}",
                fromCurrencyAmount = "${_currencyCalculationState.value.fromCurrencyAmount}",
                toCurrencyAmount = _currencyCalculationState.value.toCurrencyAmount.toString()
            )
        }
    }



    override fun onCleared() {
        super.onCleared()
        Log.e("Api" , "HomeVIEWModel :: onClear ")

    }
}