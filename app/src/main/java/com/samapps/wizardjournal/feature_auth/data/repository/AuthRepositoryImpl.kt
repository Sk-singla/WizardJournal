package com.samapps.wizardjournal.feature_auth.data.repository

import com.samapps.wizardjournal.feature_auth.utils.TokenDataStore
import com.samapps.wizardjournal.feature_auth.data.datasource.remote.AuthApiService
import com.samapps.wizardjournal.feature_auth.data.models.AuthResponse
import com.samapps.wizardjournal.feature_auth.data.models.LoginRequest
import com.samapps.wizardjournal.feature_auth.data.models.SignupRequest
import com.samapps.wizardjournal.feature_auth.domain.repository.AuthRepository
import retrofit2.Response

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {
    override suspend fun signup(name: String, email: String, password: String): Response<AuthResponse> {
        val request = SignupRequest(name = name, email = email, password = password)
        val response = api.signup(request)
        response.body()?.let {
            tokenDataStore.saveTokens(it.token, it.refreshToken)
        }
        return response
    }

    override suspend fun login(email: String, password: String): Response<AuthResponse> {
        val request = LoginRequest(email = email, password = password)
        val response = api.login(request)
        response.body()?.let {
            tokenDataStore.saveTokens(it.token, it.refreshToken)
        }
        return response
    }

    override suspend fun logout() {
        tokenDataStore.clearTokens()
    }
}
