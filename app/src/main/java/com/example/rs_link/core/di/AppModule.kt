package com.example.rs_link.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.rs_link.data.AuthRepositoryImpl
import com.example.rs_link.data.UserPrefsLocalDataSourceImpl
import com.example.rs_link.data.UserPrefsRepositoryImpl
import com.example.rs_link.data.local.UserPrefsLocalDataSource
import com.example.rs_link.domain.repository.AuthRepository
import com.example.rs_link.domain.repository.UserPrefsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

// Define the name of the DataStore file on the device
private const val USER_PREFERENCES_FILE_NAME = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserPrefsRepository(
        userPrefsRepositoryImpl: UserPrefsRepositoryImpl
    ): UserPrefsRepository

    @Binds
    @Singleton
    abstract fun bindUserPrefsLocalDataSource(
        userPrefsLocalDataSourceImpl: UserPrefsLocalDataSourceImpl
    ): UserPrefsLocalDataSource


}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    // --- PROVIDERS (Tells Hilt how to construct the complex objects) ---
    @Provides
    @Singleton
    fun providePreferenceDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = null, // Handle corruption if needed
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()), // Uses IO thread for disk access
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_FILE_NAME) }
        )
    }
}