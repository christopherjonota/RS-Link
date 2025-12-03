package com.example.rs_link.feature_auth.email_verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_link.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmailVerificationUiState(
    val email: String = "", // <--- New field
    val isVerified: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 2. Fetch email immediately on load
        val userEmail = userRepository.getCurrentUserEmail() ?: "your email"
        _uiState.update { it.copy(email = userEmail) }
    }

    fun checkVerificationStatus() {
        viewModelScope.launch {
            try {
                // 1. Refresh user data from Firebase server
                userRepository.reloadUser()

                // 2. Check status
                if (userRepository.isEmailVerified()) {
                    _uiState.update { it.copy(isVerified = true) }
                } else {
                    // Optional: Show "Not verified yet" message
                }
            } catch (e: Exception) {
                // Handle error
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun resendEmail() {
        viewModelScope.launch {
            userRepository.sendEmailVerification()
            // Show "Email sent" snackbar
        }
    }
}