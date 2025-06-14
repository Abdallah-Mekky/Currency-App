package com.example.currency.presentation.ui.home

import android.os.Bundle
import android.util.Log
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
    private lateinit var fromCurrenciesRatesAdapter: CurrencyRateAdapter
    private lateinit var toCurrenciesRatesAdapter: CurrencyRateAdapter

//    by lazy {
//        CurrencyRateAdapter(requireContext(), mutableListOf())
//    }
//    by lazy {
//        CurrencyRateAdapter(requireContext(), mutableListOf())
//    }



    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("Api" , "HomeFragmnet :: onCrelate ")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun initViewModelObserver(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            launch { subscribeToCurrenciesRates() }
            launch { subscribeToLastTimeUpdate() }
            launch { subscribeToCurrencyCalculation() }
            launch { subscribeToUiErrors() }
        }
    }

    private suspend fun subscribeToCurrenciesRates() {
        homeViewModel.currenciesRatesState.flowWithLifecycle(lifecycle,Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { currenciesRatesList ->
                currenciesRatesList?.let { currenciesList ->
                    Log.e("Api" , "HomeFragmnet :: currenciesList = ${
                        currenciesList.joinToString()
                    } ")
                    bindCurrenciesRatesList(currenciesRatesList = currenciesList)
                }
            }
    }

    private suspend fun subscribeToLastTimeUpdate() {
        homeViewModel.lastUpdateTimeState.flowWithLifecycle(lifecycle,Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { lastUpdateTime ->
                Log.e("Api" , "HomeFragmnet :: lastUpdateTime = ${
                    lastUpdateTime
                } ")

                lastUpdateTime?.let { updatedTime ->
                    bindLastUpdateTime(updatedTime)
                }

            }
    }

    private suspend fun subscribeToCurrencyCalculation() {
        homeViewModel.currencyCalculationState.flowWithLifecycle(lifecycle,Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { currencyCalculation ->
                Log.e("Api" , "HomeFragmnet :: currencyCalculation = ${
                    currencyCalculation.toString()
                } ")

                bindConvertedCurrencyAmount(currencyCalculation.toCurrencyAmount.toString())
            }
    }

    private suspend fun subscribeToUiErrors() {
        homeViewModel.uiErrors.flowWithLifecycle(lifecycle,Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { message ->
                Log.e("Api" , "HomeFragmnet :: subscribeToUiErrors error = ${
                    message
                } ")

                bindErrorMessages(message)
            }
    }

    private fun bindErrorMessages(message: String) {
        if (message.isNotEmpty()){
            binding.root.showSnackBar(message = message)
        }
    }

//    private fun initDropDownMenusAdapters() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            binding.apply {
//                fromAutoCompleteTextView.setAdapter(fromCurrenciesRatesAdapter)
//                toAutoCompleteTextView.setAdapter(toCurrenciesRatesAdapter)
//            }
//        }
//    }

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

    private fun bindLastUpdateTime(lastUpdateTime : String) {
        viewLifecycleOwner.lifecycleScope.launch {
            "Last update time :$lastUpdateTime".also { binding.lastUpdateTimeText.text = it }
        }
    }

    private fun bindConvertedCurrencyAmount(convertedAmount: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.toAmountEditText.setText(convertedAmount)
        }
    }

    private fun handleFromCurrencyAmountChanges() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.fromAmountEditText.doAfterTextChanged {
                if (it.toString().isEmpty()){
                    homeViewModel.updateFromCurrencyAmount(0)
                } else {
                    homeViewModel.updateFromCurrencyAmount(it.toString().toInt())
                }
            }
        }
    }

    private fun handleFromCurrencyCodeUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.fromAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
                val updatedCurrency = parent.getItemAtPosition(position) as CurrenciesRatesData
                Log.d("Api", "FromCurrencyCodeUpdates Code=${updatedCurrency.currencyCode}, Name=${updatedCurrency.date}, Rate=${updatedCurrency.currencyRate}")
                homeViewModel.updateFromCurrency(fromCurrencyCode = updatedCurrency.currencyCode, fromCurrencyRate = updatedCurrency.currencyRate)
            }
        }
    }

    private fun handleToCurrencyCodeUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.toAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
                val updatedCurrency = parent.getItemAtPosition(position) as CurrenciesRatesData
                Log.d("Api", "ToCurrencyCodeUpdates Code=${updatedCurrency.currencyCode}, Name=${updatedCurrency.date}, Rate=${updatedCurrency.currencyRate}")
                homeViewModel.updateToCurrency(
                    toCurrencyCode = updatedCurrency.currencyCode,
                    toCurrencyRate = updatedCurrency.currencyRate
                )
            }
        }
    }

    private fun handleSwapButtonClick() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.swapBtn.setOnClickListener {
                binding.swapBtn.disable()
                val fromCurrencyCode = binding.fromAutoCompleteTextView.text.toString()
                val toCurrencyCode = binding.toAutoCompleteTextView.text.toString()
                val toCurrencyAmount = BigDecimal(binding.toAmountEditText.text.toString()).setScale(3, RoundingMode.HALF_UP).toInt()

                binding.fromAmountEditText.setText(toCurrencyAmount.toString())
                Log.d("Api", "handleSwapButtonClick amount = ${toCurrencyAmount}")

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

    private fun swapDropdowns(fromCurrencyCode: String, toCurrencyCode: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.fromAutoCompleteTextView.setText(toCurrencyCode,false)
            binding.toAutoCompleteTextView.setText(fromCurrencyCode,false)
        }
    }
}