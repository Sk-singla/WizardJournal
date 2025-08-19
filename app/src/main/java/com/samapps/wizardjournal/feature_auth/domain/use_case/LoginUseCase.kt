package com.samapps.wizardjournal.feature_auth.domain.use_case

import com.samapps.wizardjournal.feature_auth.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email, password)
}
