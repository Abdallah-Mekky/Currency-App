package com.example.currency.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currency.domain.usecase.currencies.LoadAllCurrenciesRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val loadAllCurrenciesRatesUseCase: LoadAllCurrenciesRatesUseCase
) : ViewModel() {

     fun loadAllCurrenciesRates() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loadAllCurrenciesRatesUseCase.invoke()
//            Log.e("Api" , "SplashViewModel :: result = ${
//                result.toString()
//            } ")
        }
    }
}