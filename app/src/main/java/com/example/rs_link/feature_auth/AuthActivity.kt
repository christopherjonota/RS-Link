package com.example.rs_link.feature_auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.rs_link.core.ui.theme.ThemeRSLink
import com.example.rs_link.feature_auth.login.SignInViewModel
import com.example.rs_link.feature_auth.registration.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.content.Intent
import com.example.rs_link.feature_dashboard.DashboardActivity

@AndroidEntryPoint
class AuthActivity : ComponentActivity(){

    private val loginViewModel: SignInViewModel by viewModels()
    private val registrationViewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){ // runs once it is visible
            }
        }
        setContent { // This will display the UI/ Content
            ThemeRSLink { //This is the theme that will be used
                AuthNavigation(
                    loginViewModel = loginViewModel,
                    registrationViewModel = registrationViewModel,
                    onLoginSuccess = {
                        navigateToDashboard()
                    }
                )

            }
        }


    }
    // Private function to handle the heavy lifting
    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        // Clear the back stack so they can't go back to Login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Close this activity explicitly
    }
}