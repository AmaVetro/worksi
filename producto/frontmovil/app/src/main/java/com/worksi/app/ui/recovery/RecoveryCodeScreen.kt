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
fun RecoveryCodeScreen(onCodeVerified: () -> Unit, onNewCode: () -> Unit, onBack: () -> Unit) {
    var code by remember { mutableStateOf("") }

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

            Text("Ingresa tu código", style = MaterialTheme.typography.headlineSmall, color = White, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Código", color = White.copy(alpha = 0.8f)) },
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCodeVerified,
                modifier = Modifier.fillMaxWidth().height(52.dp).shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent, contentColor = White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ingresar código", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("¿No recibiste el código?", color = White.copy(alpha = 0.9f), fontSize = 14.sp, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNewCode,
                modifier = Modifier.fillMaxWidth().height(52.dp).shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent, contentColor = White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enviar nuevo código", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
            }
        }

        // Flecha de volver (encima)
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