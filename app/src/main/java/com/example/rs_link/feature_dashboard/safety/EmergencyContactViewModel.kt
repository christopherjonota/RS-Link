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

@HiltViewModel
class EmergencyContactViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get the ID passed from navigation (if any)
    private val contactId: String? = savedStateHandle["contactId"]
    // UI State (Reused from before)
    private val _uiState = MutableStateFlow(AddContactUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // If we have an ID, we are in EDIT MODE. Load the data!
        if (contactId != null) {
            loadContact(contactId)
        }
    }
    private fun loadContact(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val contact = userRepository.getContactById(id)
            if (contact != null) {
                // Pre-fill the form
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        firstName = contact.firstName,
                        lastName = contact.lastName,
                        phoneNumber = contact.number
                    )
                }
            }
        }
    }

    fun saveContact() {
        val state = _uiState.value
        // ... Validation ...

        viewModelScope.launch {
            if (contactId != null) {
                // --- EDIT MODE ---
                val updatedContact = Contact(
                    id = contactId, // IMPORTANT: Keep the ID!
                    firstName = state.firstName,
                    lastName = state.lastName,
                    number = "${state.countryCode}${state.phoneNumber}",
                    relationship = state.relationship
                )
                userRepository.updateEmergencyContact(updatedContact)
            }
            _uiState.update { it.copy(isSuccess = true) }
        }
        }
    // 1. Realtime List
    val contacts = userRepository.getEmergencyContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2. Delete Action (Swipe or Button)
    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            userRepository.deleteEmergencyContact(contactId)
        }
    }


}