package com.worksi.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.local.SecureTokenStore
import com.worksi.app.data.model.LoginRequest
import com.worksi.app.data.model.LockInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lockInfo: LockInfo? = null,
    val loginSuccess: Boolean = false,
    val pendingNavigateToLocked: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val authService = RetrofitClient.authService

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun consumeLoginSuccess() {
        _uiState.update { it.copy(loginSuccess = false) }
    }

    fun consumeLockedNavigation() {
        _uiState.update { it.copy(pendingNavigateToLocked = false) }
    }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = authService.login(LoginRequest(state.email, state.password))
                when (response.code()) {
                    200 -> {
                        val body = response.body()
                        if (body != null) {
                            SecureTokenStore.saveAccessToken(body.accessToken)
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    lockInfo = body.lockInfo,
                                    loginSuccess = true)
                            }
                        } else {
                            _uiState.update {
                                it.copy(isLoading = false, errorMessage = "Respuesta invalida")
                            }
                        }
                    }
                    401, 400 -> {
                        val msg = ApiErrorParser.message(response)
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = msg)
                        }
                    }
                    422 -> {
                        val msg = ApiErrorParser.message(response)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = msg,
                                pendingNavigateToLocked = true)
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = ApiErrorParser.message(response))
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error de conexion")
                }
            }
        }
    }
}
