package com.example.currency.presentation.ui.splash

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
import androidx.navigation.fragment.findNavController
import com.example.currency.presentation.utils.ext.showSnackBar
import com.example.currency.presentation.utils.ext.showSnackBarWithAction
import com.example.currency.presentation.utils.ext.toGone
import com.example.currencytask.R
import com.example.currencytask.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    val binding get() = _binding!!

    private val splashViewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel.loadAllCurrenciesRates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModelObserver()
//        lifecycleScope.launch {
//            delay(2000)
//            findNavController().navigate(R.id.homeFragment)
//        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.progressBar.toGone()
        _binding = null
    }

    private fun initViewModelObserver(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            launch { subscribeToUiActions() }
        }
    }

    private suspend fun subscribeToUiActions() {
        splashViewModel.uiActions.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collectLatest { message ->
                Log.e("Api" , "SplashFragmnet :: uiActions = ${
                    message
                } ")
                bindUiActions(message)
            }
    }

    private fun bindUiActions(message: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (message.isNotEmpty()) {
                binding.root.showSnackBarWithAction(message = message, action = {splashViewModel.loadAllCurrenciesRates()})
            } else {
                findNavController().navigate(R.id.homeFragment)
            }
        }

    }
}