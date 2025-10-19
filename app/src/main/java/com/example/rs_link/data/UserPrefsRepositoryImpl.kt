package com.example.rs_link.data

import com.example.rs_link.data.local.UserPrefsDataSource
import com.example.rs_link.domain.repository.UserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// Mock: Implements the UserPrefsRepository contract for the data layer.
@Singleton
class UserPrefsRepositoryImpl @Inject constructor(
    private val dataSource: UserPrefsDataSource // This data source will be injected here
) : UserPrefsRepository {

    // Start as false to force the Router to go to OnboardingActivity first
    private val _hasSeenOnboarding = MutableStateFlow(false)

    override fun hasSeenOnboarding(): Flow<Boolean> = _hasSeenOnboarding.asStateFlow()

    override suspend fun setOnboardingComplete(isComplete: Boolean) {
        _hasSeenOnboarding.value = isComplete
    }
}