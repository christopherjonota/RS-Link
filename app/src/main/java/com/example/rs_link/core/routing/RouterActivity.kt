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
import com.example.rs_link.feature_dashboard.DashboardActivity
import com.example.rs_link.feature_onboarding.OnboardingActivity
import com.example.rs_link.feature_signin.SignInActivity

@AndroidEntryPoint // Used for hilt injection
class RouterActivity : ComponentActivity() {

    private val viewModel: RouterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        // Install the Splash Screen BEFORE super.onCreate()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // This is crucial: holds the splash screen until routing is done
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