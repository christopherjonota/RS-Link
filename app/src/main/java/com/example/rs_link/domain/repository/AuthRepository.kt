package com.example.rs_link.domain.repository

import com.example.rs_link.domain.model.Credentials
import com.example.rs_link.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isLoggedIn(): Flow<Boolean>

    // Core authentication operations
    suspend fun signIn(credentials: Credentials): Result<User>
    suspend fun signOut()
}