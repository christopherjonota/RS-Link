package com.example.rs_link.feature_signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


// 1. Define the UI state class
data class RegistrationUiState(
    val email: String = "",
    val password: String = "",
    val isRegistering: Boolean = false,
    val registrationError: String? = null,
    val isRegistrationSuccess: Boolean = false
)


@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel(){
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()
    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun registerUser() {
        // Simple validation check (should be more robust in a real app)
        if (_uiState.value.email.isBlank() || _uiState.value.password.isBlank()) {
            _uiState.update { it.copy(registrationError = "Email and Password cannot be empty.") }
            return
        }

        _uiState.update { it.copy(isRegistering = true, registrationError = null) }

        viewModelScope.launch {
            delay(1500) // Simulate a network registration call

            // In a real app, this would be the result of a backend call
            if (_uiState.value.password.length > 5) {
                _uiState.update { it.copy(isRegistrationSuccess = true, isRegistering = false) }
            } else {
                _uiState.update {
                    it.copy(
                        registrationError = "Password is too weak (min 6 characters).",
                        isRegistering = false
                    )
                }
            }
        }
    }
}