package com.example.rs_link.feature_auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_link.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.regex.Pattern

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null
)


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()


    fun onEmailChange(newValue: String) {
        val emailValid = Pattern.matches(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+",
            newValue
        )
        _uiState.update { it.copy(
            email = newValue,
            //emailError = if (emailValid) null else "Invalid email address"
        ) }
    }

    fun onPasswordChange(newValue: String) {
        val passwordError = when {
            newValue.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }
        _uiState.update { it.copy(
            password = newValue,
            //passwordError = passwordError,
            // Also re-validate confirm password
            //confirmPasswordError = if (newValue != it.confirmPassword) "Passwords do not match" else null
        ) }
    }

    fun login() {
        // Basic Validation
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                userRepository.login(state.email, state.password)

                // Success!
                _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }

            } catch (e: Exception) {
                // Failure (Wrong password, user not found, etc.)
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Login failed"
                ) }
            }
        }
    }

//    private fun validateAllFields(): Boolean {
//        val state = _uiState.value
//        // Trigger all validation messages
//        onFirstNameChange(state.firstName)
//        onLastNameChange(state.lastName)
//        onBirthDateChange(state.birthDate)
//        onContactNumberChange(state.contactNumber)
//        onEmailChange(state.email)
//        onPasswordChange(state.password)
//        onConfirmPasswordChange(state.confirmPassword)
//
//        // Check if any error fields are non-null
//        return state.firstNameError == null &&
//                state.lastNameError == null &&
//                state.birthDateError == null &&
//                state.contactNumberError == null &&
//                state.emailError == null &&
//                state.passwordError == null &&
//                state.confirmPasswordError == null
//    }
}