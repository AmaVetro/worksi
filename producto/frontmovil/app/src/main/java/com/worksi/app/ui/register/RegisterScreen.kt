package com.worksi.app.ui.register

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White
import com.worksi.app.validation.PasswordPolicy

@Composable
fun RegisterScreen(onNavigateBack: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var segundoNombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var nroDocumento by remember { mutableStateOf("") }
    var calle by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Contenido principal (formulario) detrás
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado inspirado en el diseño anterior
            Text(
                text = "Bienvenido a",
                color = CyanPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = CyanPrimary,
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = CyanPrimary,
                            fontSize = 16.sp
                        )
                    ) {
                        append("Tu nuevo trabajo a solo un ")
                    }
                    withStyle(
                        SpanStyle(
                            color = OrangeAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    ) {
                        append("click.")
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campos del formulario
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre (*)", color = CyanPrimary) },
                isError = showErrors && nombre.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = CyanPrimary,
                    unfocusedLabelColor = CyanPrimary.copy(alpha = 0.7f),
                    cursorColor = CyanPrimary,
                    focusedTextColor = CyanPrimary,
                    unfocusedTextColor = CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = segundoNombre,
                onValueChange = { segundoNombre = it },
                label = { Text("Segundo nombre", color = CyanPrimary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = CyanPrimary,
                    unfocusedLabelColor = CyanPrimary.copy(alpha = 0.7f),
                    cursorColor = CyanPrimary,
                    focusedTextColor = CyanPrimary,
                    unfocusedTextColor = CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = apellidoPaterno,
                onValueChange = { apellidoPaterno = it },
                label = { Text("Apellido Paterno (*)", color = CyanPrimary) },
                isError = showErrors && apellidoPaterno.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = CyanPrimary,
                    unfocusedLabelColor = CyanPrimary.copy(alpha = 0.7f),
                    cursorColor = CyanPrimary,
                    focusedTextColor = CyanPrimary,
                    unfocusedTextColor = CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = apellidoMaterno,
                onValueChange = { apellidoMaterno = it },
                label = { Text("Apellido Materno (*)", color = CyanPrimary) },
                isError = showErrors && apellidoMaterno.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = CyanPrimary,
                    unfocusedLabelColor = CyanPrimary.copy(alpha = 0.7f),
                    cursorColor = CyanPrimary,
                    focusedTextColor = CyanPrimary,
                    unfocusedTextColor = CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo (*)", color = CyanPrimary) },
                isError = showErrors && correo.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = CyanPrimary,
                    unfocusedLabelColor = CyanPrimary.copy(alpha = 0.7f),
                    cursorColor = CyanPrimary,
                    focusedTextColor = CyanPrimary,
                    unfocusedTextColor = CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (*)", color = CyanPrimary) },
                visualTransformation = PasswordVisualTransformation(),
                isError = showErrors && (password.isBlank() || !PasswordPolicy.matches(password)),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = CyanPrimary,
                    unfocusedLabelColor = CyanPrimary.copy(alpha = 0.7f),
                    cursorColor = CyanPrimary,
                    focusedTextColor = CyanPrimary,
                    unfocusedTextColor = CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "*Debe contener mínimo 10 caracteres.",
                color = CyanPrimary.copy(alpha = 0.85f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "*Debe contener al menos 1 símbolo, 1 número, letras minúsculas y mayúsculas.",
                color = CyanPrimary.copy(alpha = 0.85f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = celular,
                onValueChange = { celular = it },
                label = { Text("Celular (*)", color = CyanPrimary) },
                isError = showErrors && celular.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = CyanPrimary,
                    unfocusedLabelColor = CyanPrimary.copy(alpha = 0.7f),
                    cursorColor = CyanPrimary,
                    focusedTextColor = CyanPrimary,
                    unfocusedTextColor = CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = rut,
                onValueChange = { rut = it },
                label = { Text("Rut (*)", color = CyanPrimary) },
                isError = showErrors && rut.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = CyanPrimary,
                    unfocusedLabelColor = CyanPrimary.copy(alpha = 0.7f),
                    cursorColor = CyanPrimary,
                    focusedTextColor = CyanPrimary,
                    unfocusedTextColor = CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nroDocumento,
                onValueChange = { nroDocumento = it },
                label = { Text("Nro. Documento (*)", color = CyanPrimary) },
                isError = showErrors && nroDocumento.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = CyanPrimary,
                    unfocusedLabelColor = CyanPrimary.copy(alpha = 0.7f),
                    cursorColor = CyanPrimary,
                    focusedTextColor = CyanPrimary,
                    unfocusedTextColor = CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = calle,
                onValueChange = { calle = it },
                label = { Text("Calle (opcional)", color = CyanPrimary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = CyanPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = CyanPrimary,
                    unfocusedLabelColor = CyanPrimary.copy(alpha = 0.7f),
                    cursorColor = CyanPrimary,
                    focusedTextColor = CyanPrimary,
                    unfocusedTextColor = CyanPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            if (showErrors) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Completa los campos obligatorios.",
                    color = OrangeAccent,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Continuar
            Button(
                onClick = {
                    showErrors = true
                    if (nombre.isNotBlank() && apellidoPaterno.isNotBlank() && apellidoMaterno.isNotBlank()
                        && correo.isNotBlank() && PasswordPolicy.matches(password)
                        && celular.isNotBlank() && rut.isNotBlank() && nroDocumento.isNotBlank()
                    ) {
                        onNavigateBack()
                    }
                },
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
                    "Continuar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }

        // 🔙 Flecha de volver (encima de todo)
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = CyanPrimary
            )
        }
    }
}