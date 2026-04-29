package com.worksi.app.ui.recovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun RecoveryLockedScreen(onNavigateToRecovery: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyanPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título WorkSí grande
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 42.sp
                        )
                    ) {
                        append("Work")
                    }
                    withStyle(
                        SpanStyle(
                            color = OrangeAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 42.sp
                        )
                    ) {
                        append("SÍ")
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Mensaje de advertencia (centrado)
            Text(
                "Excediste el límite de intentos. Por nuestros lineamientos de seguridad, deberás recuperar tu contraseña.",
                color = OrangeAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,                  // <-- centrado
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // "Pulsa aquí:" (centrado automático por el column)
            Text(
                "Pulsa aquí:",
                color = OrangeAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Recuperar contraseña
            Button(
                onClick = onNavigateToRecovery,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeAccent,
                    contentColor = White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Recuperar contraseña",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }
    }
}