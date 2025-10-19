package com.example.rs_link.core.di

import com.example.rs_link.data.AuthRepositoryImpl
import com.example.rs_link.data.UserPrefsRepositoryImpl
import com.example.rs_link.domain.repository.AuthRepository
import com.example.rs_link.domain.repository.UserPrefsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
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
}