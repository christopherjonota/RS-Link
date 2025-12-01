package com.example.rs_link.feature_dashboard.safety

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_link.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SafetyViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val contactsFlow = userRepository.getEmergencyContacts()

    // 2. Convert List -> Count (Int)
    val contactCount: StateFlow<Int> = contactsFlow
        .map { list -> list.size } // Count the items
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
}