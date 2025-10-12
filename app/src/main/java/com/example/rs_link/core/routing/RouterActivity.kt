package com.example.rs_link.core.routing

import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import android.content.Intent

@AndroidEntryPoint // Used for hilt injection
class RouterActivity : ComponentActivity() {

    private val viewModel: RouterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.destination.value == Destination.Loading
        }
        // 5. Observe the destination StateFlow and navigate
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.destination.collect { destination ->
                    when (destination) {
                        is Destination.Onboarding -> navigateTo(OnboardingActivity::class.java)
                        is Destination.SignIn -> navigateTo(SignInActivity::class.java)
                        is Destination.Dashboard -> navigateTo(DashboardActivity::class.java)
                        is Destination.Loading -> { /* Do nothing, waiting for checks */ }
                    }
                }
            }
        }
    }
    // 6. Navigation and Finalization Logic
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)

        // CRITICAL: Remove the RouterActivity from the back stack immediately
        finish()
    }

    // 7. No need for setContentView(R.layout.activity_router) or any layout file
    // The UI is managed entirely by the Splash Screen API.
}