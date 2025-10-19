package com.example.rs_link.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit

import com.example.rs_link.data.local.UserPrefsLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPrefsLocalDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPrefsLocalDataSource{ // This will be the contract that will be implemented

    // Preference Keys used to save and retrieve values
    private companion object{
        val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_complete")
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

        const val DEFAULT_THEME = "System"
    }

    // These will return a live stream of data and once read will extract the value
    override fun hasSeenOnboarding(): Flow<Boolean> = dataStore.data
        .map{ preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] ?: false // If the file is empty, it return as false
    }

    override fun getThemeMode(): Flow<String> = dataStore.data
        .map { preferences ->
            preferences[THEME_MODE_KEY] ?: DEFAULT_THEME // If the user never selected a theme, it will use the default
    }

    override fun isLoggedin(): Flow<Boolean> {
        TODO("Not yet implemented")
    }



    // --- WRITE OPERATIONS (Suspend functions for safe background writing) ---

    // takes the input and will work on the background thread and updates the file
    override suspend fun setOnboardingComplete(isComplete: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = isComplete
        }
    }

    override suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }

    override suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }
}