package com.example.rs_link.core.routing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_link.data.repository.UserRepository
import com.example.rs_link.domain.repository.AuthRepository
import com.example.rs_link.domain.repository.UserPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Defines the different module routes
sealed class Destination {
    object Loading : Destination()  // for initial state, representing the splash screen
    object Onboarding : Destination()
    object SignIn : Destination()
    object Dashboard : Destination()
}

@HiltViewModel
class RouterViewModel @Inject constructor(
    private val userRepository: UserRepository, // used to check if the user is currently logged in
    private val userPrefsRepository: UserPrefsRepository // used to check if the user is already done in onboarding
) : ViewModel() {

    // Holds the current destination that is initialized to loading
    private val _destination = MutableStateFlow<Destination>(Destination.Loading)

    // Exposes the current destination
    val destination: StateFlow<Destination> = _destination

    init {
        determineInitialDestination() // created to start the routing process
    }

    private fun determineInitialDestination() {
        viewModelScope.launch {
            // delay to hold the splash screen
            delay(500)

            // Fetch required states from repositories
            val hasSeenOnboarding = userPrefsRepository.hasSeenOnboarding().first()
            val isLoggedIn = userRepository.isUserLoggedIn()

            // Implement priority routing logic
            _destination.value = when {
                !hasSeenOnboarding -> Destination.Onboarding // redirect to the onboarding if not seen yet
                !isLoggedIn -> Destination.SignIn   // redirect to sign in page if not signed in yet
                else -> Destination.Dashboard // redirect to the dashboard if the other 2 is met
            }
        }
    }
}


