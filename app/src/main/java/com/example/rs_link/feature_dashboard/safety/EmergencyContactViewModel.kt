package com.example.rs_link.feature_dashboard.safety

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_link.data.model.Contact
import com.example.rs_link.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddContactUiState(
    val firstName: String = "", // <--- Changed
    val lastName: String = "",  // <--- Changed
    val phoneNumber: String = "",
    val countryCode: String = "+63",
    val relationship: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,

    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneNumberError: String? = null,
)
@HiltViewModel
class EmergencyContactViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get the ID passed from navigation (if any)
    private val contactId: String? = savedStateHandle["contactId"]


    // Check if we are in Edit Mode
    val isEditMode: Boolean = contactId != null

    // UI State (Reused from before)
    private val _uiState = MutableStateFlow(AddContactUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // If we have an ID, we are in EDIT MODE. Load the data!
        if (isEditMode) {
            loadContactData()
        }
    }
    private fun loadContactData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val contact = userRepository.getContactById(contactId!!)

            if (contact != null) {
                val cleanNumber = contact.number
                    .removePrefix("+63")
                    .removePrefix("63") // Handle case without plus
                    .removePrefix("0")  // Handle case with leading zero
                // Pre-fill the form
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        firstName = contact.firstName,
                        lastName = contact.lastName,
                        phoneNumber = cleanNumber
                    )
                }
            }else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Contact not found") }
            }
        }
    }


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
        var numericValue = newValue.filter { it.isDigit() }

        if (numericValue.startsWith("+63") && numericValue.length > 10) {
            numericValue = numericValue.removePrefix("+63")
        }

        if (numericValue.startsWith("63") && numericValue.length > 10) {
            numericValue = numericValue.removePrefix("63")
        }

        if (numericValue.startsWith("0")) {
            numericValue = numericValue.removePrefix("0")
        }

        // 3. Update State
        // We also LIMIT the length (e.g. 15 chars) to prevent massive strings
        if (numericValue.length <= 11) {
            _uiState.update {
                it.copy(
                    phoneNumber = numericValue,
                    errorMessage = null // Clear error while typing
                )
            }
        }
    }
    fun saveContact() {
        val state = _uiState.value
        if (state.firstName.isBlank()) {
            _uiState.update { it.copy(firstNameError = "First name is required") }
            return
        }
        if (state.lastName.isBlank()) {
            _uiState.update { it.copy(lastNameError = "Last name is required") }
            return
        }
        if (state.phoneNumber.isBlank()) {
            _uiState.update { it.copy(phoneNumberError = "Phone number is required") }
            return
        }

        // Rule 2: Check exact length (11)
        if (state.phoneNumber.length != 10) {
            _uiState.update { it.copy(phoneNumberError = "Phone number must be 11 digits") }
            return
        }



        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            if (isEditMode) {
                if (contactId != null) {
                    // --- EDIT MODE ---
                    val updatedContact = Contact(
                        id = contactId, // IMPORTANT: Keep the ID!
                        firstName = state.firstName,
                        lastName = state.lastName,
                        number = "${state.countryCode}${state.phoneNumber}"
                    )
                    userRepository.updateEmergencyContact(updatedContact)
                }

            } else {
                // === CREATE NEW ===
                // We don't pass an ID; Firestore generates a new one
                userRepository.addEmergencyContact(
                    firstName = state.firstName,
                    lastName = state.lastName,
                    number = "${state.countryCode}${state.phoneNumber}"
                )
            }

            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
        }
    }
    // 1. Realtime List
    val contacts = userRepository.getEmergencyContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2. Delete Action (Swipe or Button)
    fun deleteContact() {
        if (contactId == null) return
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Call the repo
                userRepository.deleteEmergencyContact(contactId)

                // Trigger navigation back by setting Success to true
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }


}