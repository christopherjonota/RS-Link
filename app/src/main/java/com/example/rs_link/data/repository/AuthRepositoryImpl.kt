package com.example.rs_link.data.repository

import com.example.rs_link.domain.model.Credentials
import com.example.rs_link.domain.model.User
import com.example.rs_link.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    // Start as logged out, forcing navigation away from Dashboard
    private val _isLoggedIn = MutableStateFlow(false)

    override fun isLoggedIn(): Flow<Boolean> = _isLoggedIn.asStateFlow()

    override suspend fun signIn(credentials: Credentials): Result<User> {
        // Mock success
        _isLoggedIn.value = true
        val mockUser = User("1", "Test User", credentials.email)
        return Result.success(mockUser)
    }

    override suspend fun signOut() {
        _isLoggedIn.value = false
    }
}