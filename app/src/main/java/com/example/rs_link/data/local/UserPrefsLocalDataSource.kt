package com.example.rs_link.data.local

import kotlinx.coroutines.flow.Flow

interface UserPrefsLocalDataSource{

    // onboarding status
    fun hasSeenOnboarding(): Flow<Boolean>
    suspend fun setOnboardingComplete(isComplete: Boolean)

    // Theme mode
    fun getThemeMode(): Flow<String>
    suspend fun setThemeMode(mode: String)

    // Log In
    fun isLoggedin(): Flow<Boolean>
    suspend fun setLoggedIn(isLoggedIn : Boolean)

}