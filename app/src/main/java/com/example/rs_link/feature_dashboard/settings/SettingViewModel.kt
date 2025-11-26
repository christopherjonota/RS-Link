package com.example.rs_link.feature_dashboard.settings

import androidx.lifecycle.ViewModel
import com.example.rs_link.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    fun logout(onLogOut: () -> Unit){
        viewModelScope.launch{
            userRepository.logout()

            onLogOut()
        }

    }
}