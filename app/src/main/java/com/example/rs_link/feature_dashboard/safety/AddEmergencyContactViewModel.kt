package com.example.rs_link.feature_dashboard.safety

import androidx.lifecycle.ViewModel
import com.example.rs_link.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class AddContactUiState(
    val firstName: String = "", // <--- Changed
    val lastName: String = "",  // <--- Changed
    val phoneNumber: String = "",
    val countryCode: String = "+63",
    val relationship: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
@HiltViewModel
class AddEmergencyContactViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddContactUiState())
    val uiState = _uiState.asStateFlow()

    // 1. New Handlers
    fun onFirstNameChange(newValue: String) {
        _uiState.update { it.copy(firstName = newValue, errorMessage = null) }
    }

    fun onLastNameChange(newValue: String) {
        _uiState.update { it.copy(lastName = newValue, errorMessage = null) }
    }

    fun onNumberChange(newValue: String) {
        // 1. Sanitize: Allow only digits
        // This prevents copy-pasting of "(123) 456" formats from crashing your logic
        val numericValue = newValue.filter { it.isDigit() }

        // 2. Remove Leading Zero
        // If user types "0917...", we turn it into "917..."
        val finalValue = if (numericValue.startsWith("0")) {
            numericValue.removePrefix("0")
        } else {
            numericValue
        }

        // 3. Update State
        // We also LIMIT the length (e.g. 15 chars) to prevent massive strings
        if (finalValue.length <= 15) {
            _uiState.update {
                it.copy(
                    phoneNumber = finalValue,
                    errorMessage = null // Clear error while typing
                )
            }
        }
    }
    // ... phone and relationship handlers ...

    fun saveContact() {
        val state = _uiState.value

        // 2. Updated Validation
        if (state.firstName.isBlank() || state.lastName.isBlank() || state.phoneNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // 3. Pass split names to Repository
                userRepository.addEmergencyContact(
                    firstName = state.firstName,
                    lastName = state.lastName,
                    number = "${state.countryCode}${state.phoneNumber}"
                )

                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}