package com.worksi.app.ui.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worksi.app.data.api.ApiErrorParser
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.PasswordRecoveryRequestBody
import com.worksi.app.data.model.PasswordRecoveryResetBody
import com.worksi.app.data.model.PasswordRecoveryVerifyBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecoveryUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recoveryToken: String? = null
)

class RecoveryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    private val auth = RetrofitClient.authService

    fun resetFlow() {
        _uiState.value = RecoveryUiState()
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun requestCode(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Completa el correo") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val r = auth.passwordRecoveryRequest(PasswordRecoveryRequestBody(email))
                when (r.code()) {
                    200 -> {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
                    }
                    else -> {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = ApiErrorParser.message(r))
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

    fun resendCode() {
        val email = _uiState.value.email.trim()
        if (email.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val r = auth.passwordRecoveryRequest(PasswordRecoveryRequestBody(email))
                when (r.code()) {
                    200 -> _uiState.update { it.copy(isLoading = false) }
                    else -> {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = ApiErrorParser.message(r))
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

    fun verifyCode(codeRaw: String, onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val code = codeRaw.trim()
        if (email.isBlank() || code.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Completa el codigo") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val r = auth.passwordRecoveryVerify(PasswordRecoveryVerifyBody(email, code))
                when (r.code()) {
                    200 -> {
                        val body = r.body()
                        if (body != null && body.verified) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    recoveryToken = body.recoveryToken,
                                    errorMessage = null)
                            }
                            onSuccess()
                        } else {
                            _uiState.update {
                                it.copy(isLoading = false, errorMessage = "Respuesta invalida")
                            }
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = ApiErrorParser.message(r))
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

    fun resetPassword(newPassword: String, onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val token = _uiState.value.recoveryToken
        if (email.isBlank() || token.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Sesion de recuperacion invalida") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val r =
                    auth.passwordRecoveryReset(
                        PasswordRecoveryResetBody(
                            email = email,
                            recoveryToken = token,
                            newPassword = newPassword))
                when (r.code()) {
                    200 -> {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
                    }
                    else -> {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = ApiErrorParser.message(r))
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
