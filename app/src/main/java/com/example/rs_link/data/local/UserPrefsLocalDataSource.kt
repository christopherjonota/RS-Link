package com.example.rs_link.data.local

import kotlinx.coroutines.flow.Flow


// This serves as the contract which enables the delegation to work
interface UserPrefsLocalDataSource{

    // onboarding status
    fun hasSeenOnboarding(): Flow<Boolean>
    suspend fun setOnboardingComplete(isComplete: Boolean)

    // Theme mode
    fun getThemeMode(): Flow<String>
    suspend fun setThemeMode(mode: String)

}