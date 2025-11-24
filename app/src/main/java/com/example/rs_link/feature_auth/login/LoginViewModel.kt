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

    // called when the user starts to input on email text field
    fun onEmailChange(newValue: String) {
        val emailValid = Pattern.matches(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+",
            newValue
        )
        _uiState.update { it.copy(
            email = newValue,
            emailError = null // this will reset the error shown if there is an error
        ) }
    }

    // called when the user starts to input on password text field
    fun onPasswordChange(newValue: String) {
        _uiState.update { it.copy(
            password = newValue,
            passwordError = null // this will reset the error shown if there is an error
        ) }
    }

    fun login() {
        val uiState = _uiState.value // holds the state of the data class of state

        // ----- Error Validations -----

        // validates blank text field
        if (uiState.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email is required") }
            return
        }
        // validates if the format for email is correct
        else if(!Pattern.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+", uiState.email)){
            _uiState.update { it.copy(emailError = "Invalid email address") }
            return
        }

        // validate blank input on password field
        if (uiState.password.isBlank()) {
            Log.d("LoginDebug", "Password Error - Blank Field")
            _uiState.update { it.copy(passwordError = "Password is required") }
            return
        }

        // This will activates the loading screen
        _uiState.update { it.copy(isLoading = true) }
        Log.d("LoginDebug", "Required field is met, proceed and show the loading screen")


        viewModelScope.launch {
            try {
                Log.d("LoginDebug", "Calling Firebase to login...")
                // trim() the email to remove accidental spaces
                userRepository.login(uiState.email.trim(), uiState.password)

                Log.d("LoginDebug", "Firebase Login Success! Updating State. Removing the loading screen and redirects to dashboard")
                _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }

            }
            catch (e: Exception) {
                Log.e("LoginDebug", "Firebase Failed: ${e.message}", e)
                // Failure (Wrong password, user not found, etc.)
                val errorMsg = when {
                    e.message?.contains("auth credential is incorrect") == true -> "Invalid email or password"
                    e.message?.contains("USER_DISABLED") == true -> "Account disabled"
                    e.message?.contains("network error") == true -> "No internet connection. Please try again"
                    else -> "Login failed. Please try again."
                }
                _uiState.update {
                    it.copy(
                    isLoading = false,
                    errorMessage = errorMsg,
                    password = "")
                }
            }
        }
    }
    fun resetUiState() { // Reset the ui state to default values
        // Replaces the current state with default (empty) values
        _uiState.update { LoginUiState() }
    }

    fun clearErrorShown() { //this will clear the error message
        _uiState.update { it.copy(errorMessage = null) }
    }
}