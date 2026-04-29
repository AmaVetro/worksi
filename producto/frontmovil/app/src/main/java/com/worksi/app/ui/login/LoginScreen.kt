package com.worksi.app.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.DarkGreen
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRecovery: () -> Unit,
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit,
    onNavigateToLocked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) onLoginSuccess()
    }

    LaunchedEffect(uiState.isLocked) {
        if (uiState.isLocked) onNavigateToLocked()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyanPrimary)
    ) {
        // Flecha de volver
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = White, fontWeight = FontWeight.Bold, fontSize = 36.sp)) { append("Work") }
                    withStyle(SpanStyle(color = OrangeAccent, fontWeight = FontWeight.Bold, fontSize = 36.sp)) { append("SÍ") }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChanged(it); localError = null },
                label = { Text("Email o nombre de usuario", color = White.copy(alpha = 0.8f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = White,
                    unfocusedBorderColor = White.copy(alpha = 0.5f),
                    focusedLabelColor = White,
                    unfocusedLabelColor = White.copy(alpha = 0.7f),
                    cursorColor = White,
                    focusedTextColor = White,
                    unfocusedTextColor = White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChanged(it); localError = null },
                label = { Text("Contraseña", color = White.copy(alpha = 0.8f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = White,
                    unfocusedBorderColor = White.copy(alpha = 0.5f),
                    focusedLabelColor = White,
                    unfocusedLabelColor = White.copy(alpha = 0.7f),
                    cursorColor = White,
                    focusedTextColor = White,
                    unfocusedTextColor = White
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            val errorMsg = localError ?: if (!uiState.isLocked) uiState.errorMessage else null
            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMsg, color = OrangeAccent, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (uiState.email.isBlank() || uiState.password.isBlank()) localError = "Completa todos los campos"
                    else { localError = null; viewModel.login() }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp).shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = DarkGreen),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading && !uiState.isLocked
            ) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DarkGreen)
                else Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DarkGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRecovery) {
                Text("Olvidé mi contraseña", color = White.copy(alpha = 0.9f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}