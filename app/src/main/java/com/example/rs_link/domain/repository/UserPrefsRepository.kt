package com.example.rs_link.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPrefsRepository {

    // for getting the onboarding status
    fun hasSeenOnboarding(): Flow<Boolean>

    // for setting the onboarding status
    suspend fun setOnboardingComplete(isComplete: Boolean)

    // Light And Dark Mode Preference
    fun getThemeMode(): Flow<String>
    suspend fun setThemeMode(mode: String)
}