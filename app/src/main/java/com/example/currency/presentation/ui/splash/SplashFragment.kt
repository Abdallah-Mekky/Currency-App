package com.example.currency.presentation.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.currency.presentation.utils.ext.showSnackBarWithAction
import com.example.currency.presentation.utils.ext.toGone
import com.example.currencytask.R
import com.example.currencytask.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
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
    }

    /**
     * Initializes observers for the ViewModel's state flows within the fragment's lifecycle scope.
     */
    private fun initViewModelObserver() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            launch { subscribeToUiActions() }
        }
    }

    /**
     * Observes UI action messages from the SplashViewModel and passes them to the UI for handling.
     */
    private suspend fun subscribeToUiActions() {
        splashViewModel.uiActions.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .flowOn(Dispatchers.IO).collect { message ->
                bindUiActions(message)
            }
    }

    /**
     * Handles UI actions based on the received message.
     *
     * @param message The UI action message to handle.
     */
    private fun bindUiActions(message: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            if (message.isNotEmpty()) {
                binding.root.showSnackBarWithAction(
                    message = message,
                    action = { splashViewModel.loadAllCurrenciesRates() })
            } else {
//                val navOptions = NavOptions.Builder()
//                    .setEnterAnim(R.anim.slide_in_right)
//                    .setExitAnim(R.anim.slide_out_left)
//                    .setPopEnterAnim(R.anim.slide_in_left)
//                    .setPopExitAnim(R.anim.slide_out_right)
//                    .build()
//
//                findNavController().navigate(R.id.homeFragment, null, navOptions)
                findNavController().navigate(R.id.homeFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.progressBar.toGone()
        _binding = null
    }
}