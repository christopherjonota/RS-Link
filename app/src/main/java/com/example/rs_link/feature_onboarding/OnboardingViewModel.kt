package com.example.rs_link.feature_onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_link.domain.repository.UserPrefsRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPrefsRepository : UserPrefsRepository // injects the instance into this
) : ViewModel() {

    // 2. Event Channel: Used to send one-time events back to the Activity (CRITICAL)
    private val _onboardingEvent = Channel<OnboardingEvent>() // used for sending events that should be consumed once
    val onboardingEvent = _onboardingEvent.receiveAsFlow() // collected by the onboarding activity and navigates once changed

     // Called when the user presses the "Get Started" button on the final slides
    fun completeOnboarding() {
        viewModelScope.launch {
            // Save the state to DataStore/Prefs (via the repository)
            userPrefsRepository.setOnboardingComplete(true)

            // Send a one-time navigation event to the Activity
            _onboardingEvent.send(OnboardingEvent.NavigateToNextStep)
        }
    }
}
// Sealed class for one-time events
sealed class OnboardingEvent {
    object NavigateToNextStep : OnboardingEvent()
}