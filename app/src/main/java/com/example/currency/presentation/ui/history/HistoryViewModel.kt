package com.example.currency.presentation.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currency.domain.model.DayValidator
import com.example.currency.domain.model.HistoricalCurrenciesData
import com.example.currency.domain.usecase.historical.DeleteDaysAfterFourUseCase
import com.example.currency.domain.usecase.historical.GetHistoricalCurrenciesDataByDayUseCase
import com.example.currency.domain.usecase.historical.GetLastFourDaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getLastFourDaysUseCase: GetLastFourDaysUseCase,
    private val getHistoricalCurrenciesDataByDayUseCase: GetHistoricalCurrenciesDataByDayUseCase,
    private val deleteDaysAfterFourUseCase: DeleteDaysAfterFourUseCase
) : ViewModel() {

    /** To track current day selection state **/
    private val _currentDaysState = MutableStateFlow<DayValidator?>(null)
    val currentDaysState: StateFlow<DayValidator?> = _currentDaysState

    /** To track historical currencies / user transactions data state **/
    private val _historicalCurrenciesDataState =
        MutableStateFlow<List<HistoricalCurrenciesData>?>(null)
    val historicalCurrenciesDataState: StateFlow<List<HistoricalCurrenciesData>?> =
        _historicalCurrenciesDataState

    /** To control ui errors to show error one time **/
    private val _uiErrors = MutableSharedFlow<String>(replay = 1)
    val uiErrors: SharedFlow<String> = _uiErrors.asSharedFlow()

    private var dayCounter: Int? = 0 //Hold day
    private var maxDays: Int? = 3
    private lateinit var fourDaysList: List<DayValidator>

    /**
     * Retrieves the last four days of historical currency data and emits the current selected day to the UI.
     */
    fun getLastFourDays() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getLastFourDaysUseCase.invoke().collectLatest { lastFourDaysList ->
                    if (lastFourDaysList.isNotEmpty()) {
                        fourDaysList = lastFourDaysList
                        getHistoricalCurrenciesData(fourDaysList[dayCounter!!].currentDay)
                        _currentDaysState.emit(fourDaysList[dayCounter!!])
                    }
                }
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }
        }
    }

    /**
     * Advances to the next available day in the [fourDaysList] and updates the historical data accordingly.
     */
    fun getNextDay() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (dayCounter!! < maxDays!!) {
                    dayCounter = dayCounter?.inc()
                    getHistoricalCurrenciesData(fourDaysList[dayCounter!!].currentDay)
                    _currentDaysState.emit(fourDaysList[dayCounter!!])
                }
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }
        }
    }

    /**
     * Moves to the previous available day in the [fourDaysList] and updates the historical data accordingly.
     */
    fun getPreviousDay() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (dayCounter!! > 0) {
                    dayCounter = dayCounter?.dec()
                    getHistoricalCurrenciesData(fourDaysList[dayCounter!!].currentDay)
                    _currentDaysState.emit(fourDaysList[dayCounter!!])
                }
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }
        }
    }

    /**
     * Retrieves historical currency conversion data for a specific [day] and updates the UI state accordingly.
     *
     * @param day The day for which to load historical currency data (formatted as a string).
     */
    private fun getHistoricalCurrenciesData(day: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getHistoricalCurrenciesDataByDayUseCase.invoke(day = day)
                    .collect { historicalCurrenciesDataList ->
                        if (historicalCurrenciesDataList.isNotEmpty()) {
                            _historicalCurrenciesDataState.emit(historicalCurrenciesDataList)
                        } else {
                            _historicalCurrenciesDataState.emit(emptyList())
                        }

                        deleteDaysAfterFourUseCase.invoke(fourDaysList.first().currentDay)
                    }
            } catch (exception: Exception) {
                _uiErrors.emit(exception.message ?: "")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dayCounter = null
        maxDays = null
        fourDaysList = emptyList()
    }
}