package com.example.rs_link.data

import com.example.rs_link.data.local.UserPrefsLocalDataSource
import com.example.rs_link.domain.repository.AuthRepository
import com.example.rs_link.domain.repository.UserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPrefsRepositoryImpl @Inject constructor(
    private val  localDataSource: UserPrefsLocalDataSource // This data source will be injected here
) : UserPrefsRepository { // <- This is the contract that implements the function inside the repository


    // These are delegation that passes the request to the injected datasource to be managed by the UserPrefsLocalDataSourceImpl
    override fun hasSeenOnboarding(): Flow<Boolean> =
        localDataSource.hasSeenOnboarding()

    override suspend fun setOnboardingComplete(isComplete: Boolean) {
        localDataSource.setOnboardingComplete(isComplete)
    }

    override fun getThemeMode(): Flow<String> =
        localDataSource.getThemeMode()

    override suspend fun setThemeMode(mode: String) {
        localDataSource.setThemeMode(mode)
    }

}