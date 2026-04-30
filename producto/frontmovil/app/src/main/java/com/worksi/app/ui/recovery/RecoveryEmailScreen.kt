package com.worksi.app.ui.recovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White

@Composable
fun RecoveryEmailScreen(onCodeSent: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyanPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = White, fontWeight = FontWeight.Bold, fontSize = 42.sp)) { append("Work") }
                    withStyle(SpanStyle(color = OrangeAccent, fontWeight = FontWeight.Bold, fontSize = 42.sp)) { append("SÍ") }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.headlineSmall, color = White, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Ingresa el email asociado a tu cuenta y te enviaremos un código de recuperación:",
                color = White.copy(alpha = 0.9f), fontSize = 14.sp, textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; showError = false },
                label = { Text("Email", color = White.copy(alpha = 0.8f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = White,
                    unfocusedBorderColor = White.copy(alpha = 0.5f),
                    focusedLabelColor = White,
                    unfocusedLabelColor = White.copy(alpha = 0.7f),
                    cursorColor = White,
                    focusedTextColor = White,
                    unfocusedTextColor = White
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "El email no está registrado en WorkSí",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { if (email.contains("@")) onCodeSent() else showError = true },
                modifier = Modifier.fillMaxWidth().height(52.dp).shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent, contentColor = White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enviar código", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
            }
        }

        // Flecha de volver (ahora después del Column, encima de todo)
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = White)
        }
    }
}