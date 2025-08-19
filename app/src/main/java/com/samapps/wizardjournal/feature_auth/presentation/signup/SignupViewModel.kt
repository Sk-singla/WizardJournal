package com.samapps.wizardjournal.feature_auth.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samapps.wizardjournal.feature_auth.domain.use_case.AuthUseCases
import com.samapps.wizardjournal.feature_auth.presentation.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val authUseCases: AuthUseCases) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun signup(name: String, email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val response = authUseCases.signup(name, email, password)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = AuthUiState.Success(response.body()!!.token)
                } else {
                    _uiState.value = AuthUiState.Error(response.message() ?: "Signup failed")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}