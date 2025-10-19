package com.example.rs_link.feature_onboarding

import android.content.Intent
import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.rs_link.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.widget.ThemeUtils
import androidx.compose.material3.MaterialTheme
import com.example.rs_link.core.ui.theme.ThemeRSLink
import com.example.rs_link.feature_signin.SignInActivity

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    // inject the viewmodel which manages the 'complete' state
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){ // only run if the activity is visible/ started
                viewModel.onboardingEvent.collect { event -> // observes the event
                    if (event is OnboardingEvent.NavigateToNextStep) { // checks if the user is already done on the onboarding
                        navigateToSignIn() // if already done, navigate now to sign in
                    }
                }
            }
        }
        setContent { // displays the ui
            ThemeRSLink {
                OnboardingScreen(viewModel = viewModel)
            }
        }
    }

    private fun navigateToSignIn() {
        // Launches the next step in the application flow
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)

        // Remove this Activity from the back stack, as the flow is finished
        finish()
    }
}