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
import android.util.Log

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null,


    // Validation error states
    // null means no error
    val emailError: String? = null,
    val passwordError: String? = null,
)


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    fun onEmailChange(newValue: String) {
        val emailValid = Pattern.matches(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+",
            newValue
        )
        _uiState.update { it.copy(
            email = newValue,
            emailError = if (emailValid) null else "Invalid email address"
        ) }
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update { it.copy(
            password = newValue,
        ) }
    }

    fun login() {
        Log.d("LoginDebug", "Clicked the login button")

        val state = _uiState.value // holds the state of the data class of state

        // Error Validations
        if (state.email.isBlank()) {
            Log.d("LoginDebug", "Email Error - Blank Field")
            _uiState.update { it.copy(emailError = "Email is required") }
            return
        }
        if (state.password.isBlank()) {
            Log.d("LoginDebug", "Password Error - Blank Field")
            _uiState.update { it.copy(passwordError = "Password is required") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        Log.d("LoginDebug", "Required field is met, proceed and show the loading screen")

        viewModelScope.launch {
            try {
                Log.d("LoginDebug", "Calling Firebase to login...")
                // trim() the email to remove accidental spaces
                userRepository.login(state.email.trim(), state.password)

                Log.d("LoginDebug", "Firebase Login Success! Updating State. Removing the loading screen and redirects to dashboard")
                _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }

            } catch (e: Exception) {
                Log.e("LoginDebug", "Firebase Failed: ${e.message}", e)
                // Failure (Wrong password, user not found, etc.)
                val errorMsg = when {
                    e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true -> "Invalid email or password"
                    e.message?.contains("USER_DISABLED") == true -> "Account disabled"
                    e.message?.contains("network error") == true -> "No internet connection"
                    else -> "Login failed. Please try again."
                }
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = errorMsg,
                    password = ""
                ) }
            }
        }
    }
    fun resetUiState() {
        // Replaces the current state with default (empty) values
        _uiState.update { LoginUiState() }
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