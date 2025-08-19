package com.samapps.wizardjournal.feature_auth.domain.use_case

data class AuthUseCases(
    val login: LoginUseCase,
    val signup: SignupUseCase,
)