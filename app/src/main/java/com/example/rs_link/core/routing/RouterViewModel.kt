package com.example.rs_link.core.routing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_link.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class Destination {
    object Loading : Destination()
    object Onboarding : Destination()
    object SignIn : Destination()
    object Dashboard : Destination()
}

@HiltViewModel
class RouterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPrefsRepository: UserPrefsRepository
) : ViewModel() {
    private val _destination = MutableStateFlow<Destination>(Destination.Loading)
    val destination: StateFlow<Destination> = _destination

    init {
        determineInitialDestination()
    }

    private fun determineInitialDestination() {
        viewModelScope.launch {
            // brief delay to hold the splash screen
            // OPTIONAL: Add a minimum delay to ensure branding is visible
            delay(500)

            // 2. Fetch required states from repositories
            val hasSeenOnboarding = userPrefsRepository.hasSeenOnboarding()
            val isLoggedIn = authRepository.isLoggedIn()

            // 3. Implement priority routing logic
            _destination.value = when {
                !hasSeenOnboarding -> Destination.Onboarding
                !isLoggedIn -> Destination.SignIn
                else -> Destination.Dashboard
            }
        }
    }
}