package com.samapps.wizardjournal.feature_auth.data.models

data class AuthResponse(
    val token: String,
    val refreshToken: String
)
