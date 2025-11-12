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

@AndroidEntryPoint
class AuthActivity : ComponentActivity(){

    private val viewModel: SignInViewModel by viewModels()
    private val registerViewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){ // runs once it is visible
            }
        }
        setContent { // This will display the UI/ Content
            ThemeRSLink { //This is the theme that will be used
                AuthNavigation(viewModel = viewModel, registerViewModel) // Start the Composable function and passing the viewmodel
            }
        }
    }
}