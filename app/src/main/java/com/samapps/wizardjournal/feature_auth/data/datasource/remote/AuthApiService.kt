package com.samapps.wizardjournal.feature_auth.data.datasource.remote

import com.samapps.wizardjournal.feature_auth.data.models.AuthResponse
import com.samapps.wizardjournal.feature_auth.data.models.LoginRequest
import com.samapps.wizardjournal.feature_auth.data.models.RefreshTokenRequest
import com.samapps.wizardjournal.feature_auth.data.models.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<AuthResponse>

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    // Removed getAllJournals as it doesn't belong in auth
    // @GET("/journal/all")
    // suspend fun getAllJournals(): Response<List<Journal>>
}
