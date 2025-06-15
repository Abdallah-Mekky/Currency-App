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

    /** To control actions and do one time event **/
    private val _uiActions = MutableSharedFlow<String>(replay = 1)
    val uiActions: SharedFlow<String> = _uiActions.asSharedFlow()

    /**
     * Loads all currency rates by invoking the use case and emits UI actions based on the result.
     **/
    fun loadAllCurrenciesRates() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = loadAllCurrenciesRatesUseCase.invoke()) {
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
            }
        }
    }
}