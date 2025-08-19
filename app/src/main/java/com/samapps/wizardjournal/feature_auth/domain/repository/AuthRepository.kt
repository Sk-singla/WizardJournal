package com.samapps.wizardjournal.feature_auth.domain.repository

import com.samapps.wizardjournal.feature_auth.data.models.AuthResponse
import retrofit2.Response

interface AuthRepository {
    suspend fun signup(name: String, email: String, password: String): Response<AuthResponse>
    suspend fun login(email: String, password: String): Response<AuthResponse>
    suspend fun logout()
}
