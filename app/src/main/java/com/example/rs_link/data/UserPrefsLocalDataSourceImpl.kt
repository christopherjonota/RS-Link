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
) : UserPrefsLocalDataSource{

    // Preference Keys
    private companion object{
        val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_complete")
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

        const val DEFAULT_THEME = "System"
    }

    override fun hasSeenOnboarding(): Flow<Boolean> = dataStore.data
        .map{ preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] ?: false
    }

    override fun getThemeMode(): Flow<String> = dataStore.data.map {
        preferences -> preferences[THEME_MODE_KEY] ?: DEFAULT_THEME
    }

    override fun isLoggedin(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    // --- WRITE OPERATIONS (Suspend functions for safe background writing) ---

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