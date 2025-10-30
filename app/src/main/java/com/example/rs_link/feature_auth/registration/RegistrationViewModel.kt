package com.example.rs_link.feature_auth.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_link.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import com.example.rs_link.data.model.User
// Holds all the state for the RegistrationScreen.
data class RegistrationUiState(
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: Long? = null, // Store as milliseconds
    val contactNumber: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Validation error states
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val birthDateError: String? = null,
    val contactNumberError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,

    // Overall state
    val isLoading: Boolean = false,
    val isRegistrationSuccess: Boolean = false,
    val generalError: String? = null
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()
    // --- Event Handlers for UI ---

    fun onFirstNameChange(newValue: String) {
        _uiState.update { it.copy(
            firstName = newValue,
            firstNameError = if (newValue.isBlank()) "First name cannot be empty" else null
        ) }
    }

    fun onLastNameChange(newValue: String) {
        _uiState.update { it.copy(
            lastName = newValue,
            lastNameError = if (newValue.isBlank()) "Last name cannot be empty" else null
        ) }
    }

    fun onBirthDateChange(newDateMillis: Long?) {
        _uiState.update { it.copy(
            birthDate = newDateMillis,
            birthDateError = if (newDateMillis == null) "Birth date must be selected" else null
        ) }
    }

    fun onContactNumberChange(newValue: String) {
        _uiState.update { it.copy(
            contactNumber = newValue,
            contactNumberError = if (newValue.length < 10) "Invalid contact number" else null
        ) }
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
        val passwordError = when {
            newValue.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }
        _uiState.update { it.copy(
            password = newValue,
            passwordError = passwordError,
            // Also re-validate confirm password
            confirmPasswordError = if (newValue != it.confirmPassword) "Passwords do not match" else null
        ) }
    }

    fun onConfirmPasswordChange(newValue: String) {
        _uiState.update { it.copy(
            confirmPassword = newValue,
            confirmPasswordError = if (newValue != it.password) "Passwords do not match" else null
        ) }
    }

    /**
     * Called when the "Add Account" button is clicked.
     */
    fun register() {
        // Run final validation check on all fields
        if (!validateAllFields()) {
            return // Stop if any field is invalid
        }

        // Set loading state
        _uiState.update { it.copy(isLoading = true, generalError = null) }

        // Launch a coroutine to simulate network call
        viewModelScope.launch {
            try {
                // --- THIS IS THE KEY CHANGE ---
                // 1. Create the User object from the state
                val state = _uiState.value
                val newUser = User(
                    firstName = state.firstName,
                    lastName = state.lastName,
                    birthDate = state.birthDate!!, // !! is safe because validateAllFields() checks for null
                    contactNumber = state.contactNumber,
                    email = state.email
                )

                // 2. Call the repository to do the actual work
                userRepository.registerUser(newUser, state.password)

                // --- ON SUCCESS ---
                _uiState.update { it.copy(
                    isLoading = false,
                    isRegistrationSuccess = true
                ) }

            } catch (e: Exception) {
                // --- ON FAILURE (The repository threw an exception) ---
                _uiState.update { it.copy(
                    isLoading = false,
                    generalError = "Registration failed: ${e.message}"
                ) }
            }

        }
    }

    private fun validateAllFields(): Boolean {
        val state = _uiState.value
        // Trigger all validation messages
        onFirstNameChange(state.firstName)
        onLastNameChange(state.lastName)
        onBirthDateChange(state.birthDate)
        onContactNumberChange(state.contactNumber)
        onEmailChange(state.email)
        onPasswordChange(state.password)
        onConfirmPasswordChange(state.confirmPassword)

        // Check if any error fields are non-null
        return state.firstNameError == null &&
                state.lastNameError == null &&
                state.birthDateError == null &&
                state.contactNumberError == null &&
                state.emailError == null &&
                state.passwordError == null &&
                state.confirmPasswordError == null
    }
}