package com.example.currencyTask.presentation.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.currencyTask.presentation.utils.ext.isVisible
import com.example.currencytask.R
import com.example.currencytask.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initBottomNavigation()
        controlBottomNavVisibility()
    }

    /**
     * Init bottom navigation view
     */
    private fun initBottomNavigation() {
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            navigateToDestination(item, navHostFragment.navController)
        }
    }

    /**
     * Handles navigation to the selected destination using the provided [NavController].
     *
     * @param item The [MenuItem] selected in the bottom navigation bar.
     * @param navController The [NavController] responsible for performing the navigation.
     *
     * @return `true` if the navigation was successful, `false` if the destination could not be found.
     */
    private fun navigateToDestination(item: MenuItem, navController: NavController): Boolean {
        val options = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .apply {
                if (item.order and Menu.CATEGORY_SECONDARY == 0) {
                    setPopUpTo(R.id.homeFragment, false, true)
                }
            }
            .build()

        return runCatching {
            navController.navigate(item.itemId, null, options)
        }.isSuccess
    }

    /**
     * Controls the visibility of the bottom navigation bar based on the current navigation destination.
     */
    private fun controlBottomNavVisibility() {
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            with(binding) {
                when (destination.id) {

                    R.id.splashFragment -> {
                        bottomNavigation.isVisible(false)
                    }

                    else -> {
                        bottomNavigation.isVisible(true)
                    }
                }
            }
        }
    }

}