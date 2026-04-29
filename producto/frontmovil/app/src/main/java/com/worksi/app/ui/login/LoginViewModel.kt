package com.worksi.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val attempts: Int = 0,
    val isLocked: Boolean = false,
    val loginSuccess: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val authService = RetrofitClient.authService

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun login() {
        val state = _uiState.value
        if (state.isLocked || state.email.isBlank() || state.password.isBlank()) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                val response = authService.login(LoginRequest(state.email, state.password))
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true)
                } else {
                    val newAttempts = state.attempts + 1
                    val locked = newAttempts >= 4
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        attempts = newAttempts,
                        isLocked = locked,
                        errorMessage = if (locked) "Excediste el límite de intentos" else "Credenciales incorrectas"
                    )
                }
            } catch (e: Exception) {
                val newAttempts = state.attempts + 1
                val locked = newAttempts >= 4
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    attempts = newAttempts,
                    isLocked = locked,
                    errorMessage = if (locked) "Excediste el límite de intentos" else "Error de conexión"
                )
            }
        }
    }
}