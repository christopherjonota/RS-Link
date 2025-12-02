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


@HiltViewModel
class AddEmergencyContactViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddContactUiState())
    val uiState = _uiState.asStateFlow()


    // ... phone and relationship handlers ...

    fun saveContact() {
        val state = _uiState.value

        // 2. Updated Validation


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