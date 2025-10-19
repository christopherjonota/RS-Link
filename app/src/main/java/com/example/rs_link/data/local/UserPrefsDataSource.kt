package com.example.rs_link.data.local

import android.preference.Preference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserPrefsDataSource{

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