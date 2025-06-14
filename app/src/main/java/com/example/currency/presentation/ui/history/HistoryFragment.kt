package com.example.currency.presentation.ui.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.currency.domain.model.DayValidator
import com.example.currency.domain.model.HistoricalCurrenciesData
import com.example.currency.presentation.ui.adapter.HistoricalCurrenciesAdapter
import com.example.currency.presentation.utils.ext.toGone
import com.example.currency.presentation.utils.ext.toVisible
import com.example.currencytask.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    val binding get() = _binding!!

    private var historicalCurrenciesAdapter: HistoricalCurrenciesAdapter? = null
    private val historyViewModel by viewModels<HistoryViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyViewModel.getLastFourDays()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModelObserver()
        initClicks()
        initHistoricalCurrenciesRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        historicalCurrenciesAdapter = null
        _binding = null
    }

    private fun initViewModelObserver(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            launch { subscribeToLastFourDays() }
            launch { subscribeToHistoricalCurrenciesData() }
        }
    }

    private suspend fun subscribeToLastFourDays() {
        historyViewModel.currentDaysState.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { currentDay ->
                currentDay?.let { day ->
                    Log.e("Api" , "HistoryFragmnet :: single day = ${
                        day.toString()
                    } ")

                    bindCurrentDay(day = day)
                }
            }
    }

    private suspend fun subscribeToHistoricalCurrenciesData() {
        historyViewModel.historicalCurrenciesDataState.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { historicalCurrenciesDataList ->

                historicalCurrenciesDataList?.let { historicalCurrenciesList ->

                    if (historicalCurrenciesList.isEmpty()) {
                        Log.e("Api" , "HistoryFragmnet :: if historicalCurrenciesDataList = ${
                            historicalCurrenciesDataList.joinToString()
                        } ")
                        bindEmptyHistoricalCurrenciesData()
                    } else {
                        Log.e("Api" , "HistoryFragmnet :: else historicalCurrenciesDataList = ${
                            historicalCurrenciesDataList.joinToString()
                        } ")
                        bindHistoricalCurrenciesData(historicalCurrenciesList)
                    }
                }
            }
    }

    private fun bindCurrentDay(day : DayValidator) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.apply {
                currentDay.text = day.currentDay
                if (!day.prev) backArrow.toGone() else backArrow.toVisible()
                if (!day.after) forwardArrow.toGone() else forwardArrow.toVisible()
            }
        }
    }

    private fun bindEmptyHistoricalCurrenciesData() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.noData.toVisible()
            binding.historicalCurrenciesRV.toGone()
        }
    }

    private fun bindHistoricalCurrenciesData(historicalCurrenciesList: List<HistoricalCurrenciesData>) {
        viewLifecycleOwner.lifecycleScope.launch {
            historicalCurrenciesAdapter?.submitList(historicalCurrenciesList)
            binding.noData.toGone()
            binding.historicalCurrenciesRV.toVisible()
        }
    }


    private fun initClicks() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.apply {
                backArrow.setOnClickListener {
                    historyViewModel.getPreviousDay()
                }

                forwardArrow.setOnClickListener {
                    historyViewModel.getNextDay()
                }
            }
        }
    }

    private fun initHistoricalCurrenciesRecyclerView() {
        historicalCurrenciesAdapter = historicalCurrenciesAdapter ?: HistoricalCurrenciesAdapter()
        binding.historicalCurrenciesRV.adapter = historicalCurrenciesAdapter
    }
}