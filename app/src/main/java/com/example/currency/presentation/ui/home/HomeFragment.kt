package com.example.currency.presentation.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.currency.domain.model.CurrenciesRatesData
import com.example.currency.presentation.ui.adapter.CurrencyRateAdapter
import com.example.currency.presentation.utils.ext.disable
import com.example.currency.presentation.utils.ext.enable
import com.example.currency.presentation.utils.ext.showSnackBar
import com.example.currencytask.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!

    private val homeViewModel by viewModels<HomeViewModel>()
    private var fromCurrenciesRatesAdapter: CurrencyRateAdapter? = null
    private var toCurrenciesRatesAdapter: CurrencyRateAdapter? = null

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel.getAllCurrenciesRates()
        homeViewModel.updateCurrencyCalculation(
            fromCurrencyCode = "USD",
            toCurrencyCode = "EGP",
            fromCurrencyAmount = 1
        )
        homeViewModel.getLastUpdateTime()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModelObserver()
        handleBackPress()
        handleFromCurrencyAmountChanges()
        handleFromCurrencyCodeUpdates()
        handleToCurrencyCodeUpdates()
        handleSwapButtonClick()
    }

    /**
     * Handle when user click back
     */
    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    /**
     * Initializes observers for the ViewModel's state flows within the fragment's lifecycle scope.
     */
    private fun initViewModelObserver() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            launch { subscribeToCurrenciesRates() }
            launch { subscribeToLastTimeUpdate() }
            launch { subscribeToCurrencyCalculation() }
            launch { subscribeToUiErrors() }
        }
    }

    /**
     * Observes the latest currency rates from the ViewModel and updates the UI.
     */
    private suspend fun subscribeToCurrenciesRates() {
        homeViewModel.currenciesRatesState.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { currenciesRatesList ->
                currenciesRatesList?.let { currenciesList ->
                    bindCurrenciesRatesList(currenciesRatesList = currenciesList)
                }
            }
    }

    /**
     * Sets up and binds the currency rates list to the from/to dropdown adapters in the UI.
     *
     * @param currenciesRatesList
     */
    private fun bindCurrenciesRatesList(currenciesRatesList: List<CurrenciesRatesData>) {
        viewLifecycleOwner.lifecycleScope.launch {
            fromCurrenciesRatesAdapter = CurrencyRateAdapter(
                requireContext(),
                currenciesRatesList
            )

            toCurrenciesRatesAdapter = CurrencyRateAdapter(
                requireContext(),
                currenciesRatesList
            )

            binding.apply {
                fromAutoCompleteTextView.setAdapter(fromCurrenciesRatesAdapter)
                toAutoCompleteTextView.setAdapter(toCurrenciesRatesAdapter)
            }
        }
    }

    /**
     * Observes the last update time from the ViewModel and updates the UI.
     */
    private suspend fun subscribeToLastTimeUpdate() {
        homeViewModel.lastUpdateTimeState.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { lastUpdateTime ->
                lastUpdateTime?.let { updatedTime ->
                    bindLastUpdateTime(updatedTime)
                }
            }
    }

    /**
     * Updates the UI with the given last update time.
     *
     * @param lastUpdateTime
     */
    private fun bindLastUpdateTime(lastUpdateTime: String) {
        viewLifecycleOwner.lifecycleScope.launch {
             binding.lastUpdateTimeText.text = lastUpdateTime
        }
    }

    /**
     * Observes currency calculation results from the ViewModel and updates the UI.
     */
    private suspend fun subscribeToCurrencyCalculation() {
        homeViewModel.currencyCalculationState.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { currencyCalculation ->
                bindConvertedCurrencyAmount(currencyCalculation.toCurrencyAmount.toString())
            }
    }

    /**
     * Updates the "to amount" EditText with the converted currency amount.
     *
     * @param convertedAmount The calculated currency amount to display.
     */
    private fun bindConvertedCurrencyAmount(convertedAmount: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.toAmountEditText.setText(convertedAmount)
        }
    }

    /**
     * Observes UI error messages from the ViewModel and displays them.
     */
    private suspend fun subscribeToUiErrors() {
        homeViewModel.uiErrors.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { message ->
                bindErrorMessages(message)
            }
    }

    /**
     * Displays an error message in a Snack-bar if the message is not empty.
     *
     * @param message The error message to show.
     */
    private fun bindErrorMessages(message: String) {
        if (message.isNotEmpty()) {
            binding.root.showSnackBar(message = message)
        }
    }

    /**
     * Listens for changes in the "from amount" EditText and updates the ViewModel.
     *
     * If the input is empty, sets the amount to 0.
     * Otherwise, converts the input to an integer and updates it in the ViewModel.
     */
    private fun handleFromCurrencyAmountChanges() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.fromAmountEditText.doAfterTextChanged {
                if (it.toString().isEmpty()) {
                    homeViewModel.updateFromCurrencyAmount(0)
                } else {
                    homeViewModel.updateFromCurrencyAmount(it.toString().toInt())
                }
            }
        }
    }

    /**
     * Handles selection changes for the "from" currency dropdown.
     *
     * When a currency is selected, updates the ViewModel with the selected
     * currency code and rate.
     */
    private fun handleFromCurrencyCodeUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.fromAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
                val updatedCurrency = parent.getItemAtPosition(position) as CurrenciesRatesData
                homeViewModel.updateFromCurrency(
                    fromCurrencyCode = updatedCurrency.currencyCode,
                    fromCurrencyRate = updatedCurrency.currencyRate
                )
            }
        }
    }

    /**
     * Handles selection changes for the "to" currency dropdown.
     *
     * When a currency is selected, updates the ViewModel with the selected
     * currency code and rate.
     */
    private fun handleToCurrencyCodeUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.toAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
                val updatedCurrency = parent.getItemAtPosition(position) as CurrenciesRatesData
                homeViewModel.updateToCurrency(
                    toCurrencyCode = updatedCurrency.currencyCode,
                    toCurrencyRate = updatedCurrency.currencyRate
                )
            }
        }
    }

    /**
     * Handles the swap button click to exchange the selected currencies.
     *
     * Swaps the "from" and "to" currency codes, updates the amount field,
     * updates the ViewModel with the new values, and updates the dropdowns.
     */
    private fun handleSwapButtonClick() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.swapBtn.setOnClickListener {
                binding.swapBtn.disable()
                val fromCurrencyCode = binding.fromAutoCompleteTextView.text.toString()
                val toCurrencyCode = binding.toAutoCompleteTextView.text.toString()
                val toCurrencyAmount =
                    BigDecimal(binding.toAmountEditText.text.toString()).setScale(
                        3,
                        RoundingMode.HALF_UP
                    ).toInt()

                binding.fromAmountEditText.setText(toCurrencyAmount.toString())

                homeViewModel.updateCurrencyCalculation(
                    fromCurrencyCode = toCurrencyCode,
                    toCurrencyCode = fromCurrencyCode,
                    fromCurrencyAmount = toCurrencyAmount
                )
                swapDropdowns(fromCurrencyCode = fromCurrencyCode, toCurrencyCode = toCurrencyCode)
                binding.swapBtn.enable()
            }
        }
    }

    /**
     * Swaps the selected values in the "from" and "to" currency dropdowns.
     *
     * Updates the UI to reflect the swapped currency codes.
     *
     * @param fromCurrencyCode The original "from" currency code.
     * @param toCurrencyCode The original "to" currency code.
     */
    private fun swapDropdowns(fromCurrencyCode: String, toCurrencyCode: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.fromAutoCompleteTextView.setText(toCurrencyCode, false)
            binding.toAutoCompleteTextView.setText(fromCurrencyCode, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fromCurrenciesRatesAdapter = null
        toCurrenciesRatesAdapter = null
        _binding = null
    }
}