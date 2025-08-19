package com.samapps.wizardjournal.feature_auth.domain.use_case

import com.samapps.wizardjournal.feature_auth.domain.repository.AuthRepository

class SignupUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String) = repository.signup(name, email, password)
}
