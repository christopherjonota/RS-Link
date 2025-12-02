package com.example.rs_link.feature_auth.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_link.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isVerified = MutableStateFlow(false)
    val isVerified = _isVerified.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun checkVerificationStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Refresh user data from Firebase server
                userRepository.reloadUser()

                // 2. Check status
                if (userRepository.isEmailVerified()) {
                    _isVerified.value = true
                } else {
                    // Optional: Show "Not verified yet" message
                }
            } catch (e: Exception) {
                // Handle error
            }
            _isLoading.value = false
        }
    }

    fun resendEmail() {
        viewModelScope.launch {
            userRepository.sendEmailVerification()
            // Show "Email sent" snackbar
        }
    }
}