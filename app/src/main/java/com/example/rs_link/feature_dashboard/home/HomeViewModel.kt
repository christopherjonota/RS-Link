package com.example.rs_link.feature_dashboard.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_link.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    // Default to "Rider" while loading
    private val _userName = MutableStateFlow("Rider")
    val userName = _userName.asStateFlow()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            // 1. Get the ID of the logged-in user
            val userId = userRepository.getCurrentUserId()
            Log.d("testing", "eto ay $userId" )
            if (userId != null) {
                // 2. Ask Repository for the profile data
                val userProfile = userRepository.getUserProfile(userId)

                // 3. If found, update the UI
                if (userProfile != null) {
                    _userName.value = userProfile.firstName
                }
                Log.d("testing", "eto ay $userProfile.firstName" )
            }
        }
    }
    private val _hasNotifications = MutableStateFlow(true) // Mock: True for testing
    val hasNotifications = _hasNotifications.asStateFlow()
}