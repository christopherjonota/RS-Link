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
    private val userPrefsRepository : UserPrefsRepository
) : ViewModel() {

    // 1. Page State: Tracks the current page (e.g., for HorizontalPager)
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    // 2. Event Channel: Used to send one-time events back to the Activity (CRITICAL)
    private val _onboardingEvent = Channel<OnboardingEvent>()
    val onboardingEvent = _onboardingEvent.receiveAsFlow()

    fun onPageChanged(page: Int) {
        _currentPage.value = page
    }

    /**
     * Called when the user presses the "Get Started" button on the final slide.
     */
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