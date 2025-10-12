package com.example.rs_link.data.repository

import com.example.rs_link.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val tokenStore: TokenStore
) : AuthRepository{
}