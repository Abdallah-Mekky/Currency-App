package com.example.currency.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currency.core.exceptions.Result
import com.example.currency.domain.usecase.currencies.LoadAllCurrenciesRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val loadAllCurrenciesRatesUseCase: LoadAllCurrenciesRatesUseCase
) : ViewModel() {

//    private val _uiActions = MutableSharedFlow<Unit>()
//    val uiActions: SharedFlow<Unit> = _uiActions.asSharedFlow()

    private val _uiActions = MutableSharedFlow<String>(replay = 1)
    val uiActions: SharedFlow<String> = _uiActions.asSharedFlow()



     fun loadAllCurrenciesRates() {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = loadAllCurrenciesRatesUseCase.invoke()) {
                is Result.Success<Unit> -> {
                 _uiActions.emit("")
                }

                is Result.ApiException -> {
                    _uiActions.emit(result.message)
                }

                is Result.NetworkException -> {
                    _uiActions.emit(result.message)
                }

                is Result.UnexpectedException -> {
                    _uiActions.emit(result.message)
                }

                else -> {Unit}
            }
        }
    }

    fun checkCurrenciesRatesNeededUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            val lastUpdatedTime = getLastUpdatedTimeUseCase.invoke()
            val now = System.currentTimeMillis()
            val twentyFourHours = 24 * 60 * 60 * 1000L

            if (now - lastUpdatedTime > twentyFourHours) {
                //_uiErrors.emit(Unit)
            }
        }
    }
}