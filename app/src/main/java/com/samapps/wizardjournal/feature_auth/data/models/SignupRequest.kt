package com.samapps.wizardjournal.feature_auth.data.models

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)