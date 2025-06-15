package com.example.currency.presentation.ui.history

import android.os.Bundle
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
import com.example.currency.presentation.utils.ext.showSnackBar
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

    /**
     * Initializes observers for the ViewModel's state flows within the fragment's lifecycle scope.
     */
    private fun initViewModelObserver() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            launch { subscribeToLastFourDays() }
            launch { subscribeToHistoricalCurrenciesData() }
            launch { subscribeToUiErrors() }
        }
    }

    /**
     * Subscribes to updates from [historyViewModel.currentDaysState] to observe the latest list of historical days.
     *
     * This function collects the state flow only when the lifecycle is in the [Lifecycle.State.RESUMED] state,
     * ensuring UI updates are performed only when the fragment is visible.
     *
     * The collection runs on [Dispatchers.IO] to ensure efficient background processing.
     */
    private suspend fun subscribeToLastFourDays() {
        historyViewModel.currentDaysState.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { currentDay ->
                currentDay?.let { day ->
                    bindCurrentDay(day = day)
                }
            }
    }

    /**
     * Binds the given [DayValidator] data to the UI components responsible for displaying the current day
     * and navigation arrows.
     *
     * This function:
     * - Updates the displayed date using [day.currentDay].
     * - Controls the visibility of the back and forward arrows based on [day.prev] and [day.after].
     *
     * It launches within the [viewLifecycleOwner]'s lifecycle scope to ensure safe execution
     * tied to the fragment's view lifecycle.
     *
     * @param day A [DayValidator] object containing the current day and flags indicating if
     * previous or next days are available for navigation.
     */
    private fun bindCurrentDay(day: DayValidator) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.apply {
                currentDay.text = day.currentDay
                if (!day.prev) backArrow.toGone() else backArrow.toVisible()
                if (!day.after) forwardArrow.toGone() else forwardArrow.toVisible()
            }
        }
    }

    /**
     * Subscribes to updates from [historyViewModel.historicalCurrenciesDataState] to observe
     * historical currency conversion data for a selected day.
     *
     * This function:
     * - Collects the data only when the lifecycle is in [Lifecycle.State.RESUMED], ensuring the UI is active.
     * - Executes the collection on [Dispatchers.IO] for efficient background processing.
     * */
    private suspend fun subscribeToHistoricalCurrenciesData() {
        historyViewModel.historicalCurrenciesDataState.flowWithLifecycle(
            lifecycle,
            Lifecycle.State.RESUMED
        )
            .flowOn(Dispatchers.IO).collectLatest { historicalCurrenciesDataList ->

                historicalCurrenciesDataList?.let { historicalCurrenciesList ->

                    if (historicalCurrenciesList.isEmpty()) {
                        bindEmptyHistoricalCurrenciesData()
                    } else {
                        bindHistoricalCurrenciesData(historicalCurrenciesList)
                    }
                }
            }
    }

    /**
     * Updates the UI to reflect that no historical currency data is available.
     *
     * This function:
     * - Shows a "no data" placeholder view ([binding.noData]).
     * - Hides the RecyclerView ([binding.historicalCurrenciesRV]) that normally displays data.
     */
    private fun bindEmptyHistoricalCurrenciesData() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.noData.toVisible()
            binding.historicalCurrenciesRV.toGone()
        }
    }

    /**
     * Binds the provided list of [HistoricalCurrenciesData] to the RecyclerView and updates the UI accordingly.
     *
     * This function:
     * - Submits the list to [historicalCurrenciesAdapter] to display historical currency transactions.
     * - Hides the "no data" placeholder view.
     * - Makes the RecyclerView visible.
     *
     * @param historicalCurrenciesList The list of historical currency data to display.
     */
    private fun bindHistoricalCurrenciesData(historicalCurrenciesList: List<HistoricalCurrenciesData>) {
        viewLifecycleOwner.lifecycleScope.launch {
            historicalCurrenciesAdapter?.submitList(historicalCurrenciesList)
            binding.noData.toGone()
            binding.historicalCurrenciesRV.toVisible()
        }
    }

    /**
     * Subscribes to UI error messages emitted by [historyViewModel.uiErrors] and binds them to the UI.
     */
    private suspend fun subscribeToUiErrors() {
        historyViewModel.uiErrors.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { message ->
                bindErrorMessages(message)
            }
    }

    /**
     * Displays the given error [message] as a snackbar if it is not empty.
     *
     * @param message The error message to display. If the message is empty, no action is taken.
     */
    private fun bindErrorMessages(message: String) {
        if (message.isNotEmpty()) {
            binding.root.showSnackBar(message = message)
        }
    }

    /**
     * Initializes click listeners for UI navigation arrows used to change the selected historical day.
     */
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

    /**
     * Initializes the RecyclerView for displaying historical currency conversion data.
     */
    private fun initHistoricalCurrenciesRecyclerView() {
        historicalCurrenciesAdapter = historicalCurrenciesAdapter ?: HistoricalCurrenciesAdapter()
        binding.historicalCurrenciesRV.adapter = historicalCurrenciesAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        historicalCurrenciesAdapter = null
        _binding = null
    }
}