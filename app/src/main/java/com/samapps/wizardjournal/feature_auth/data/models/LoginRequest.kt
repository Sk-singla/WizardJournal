package com.samapps.wizardjournal.feature_auth.data.models

data class LoginRequest(
    val email: String,
    val password: String
)