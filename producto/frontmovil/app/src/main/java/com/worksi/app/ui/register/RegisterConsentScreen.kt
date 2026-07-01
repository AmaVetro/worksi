package com.worksi.app.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White
import com.worksi.app.ui.theme.worksiOnCyanCheckboxColors

private val RegisterBackButtonRowHeight = 56.dp

@Composable
fun RegisterConsentScreen(
    viewModel: CandidateRegisterViewModel,
    onBack: () -> Unit,
    onRegistered: () -> Unit
) {
    val state by viewModel.ui.collectAsState()
    val d = state.draft
    val scroll = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyanPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, top = RegisterBackButtonRowHeight, end = 24.dp, bottom = 24.dp)
                .verticalScroll(scroll)
        ) {
            Text("Uso de datos", style = MaterialTheme.typography.headlineSmall, color = White)
            Spacer(Modifier.height(16.dp))
            Text(
                "Al crear tu cuenta en WorkSí autorizas el tratamiento de tus datos personales para gestionar tu perfil de postulante, almacenar tu CV y mostrarte ofertas laborales compatibles con tus preferencias.\n\n" +
                    "Los datos que envías (identidad, contacto, ubicación, rubro, skills, modalidades y cargas horarias preferidas, años de experiencia y renta esperada) se usan para operar la plataforma. El contenido de tu CV y tus preferencias de modalidad, jornada y experiencia pueden influir en el porcentaje de compatibilidad con las ofertas. Las skills declaradas en tu perfil y tu presentación personal no alteran ese porcentaje.\n\n" +
                    "Tus datos se comparten con reclutadores de empresas solo cuando postulas a una oferta o cuando mantienes una conversación iniciada por ellos. Otros candidatos no pueden ver tu información. WorkSí conserva tu consentimiento con fecha y hora en nuestros sistemas.\n\n" +
                    "Puedes actualizar tu perfil desde la app una vez creada la cuenta. Esta es la última pantalla antes del alta: al pulsar Completar se creará tu cuenta y se guardará tu CV en el servidor.",
                color = White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = d.consentAccepted,
                    onCheckedChange = { v -> viewModel.updateDraft { it.copy(consentAccepted = v) } },
                    colors = worksiOnCyanCheckboxColors()
                )
                Text(
                    "He leído y acepto el uso de mis datos personales conforme a la Política de Privacidad",
                    color = White,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (state.registerError != null) {
                Spacer(Modifier.height(8.dp))
                Text(state.registerError!!, color = OrangeAccent, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Al pulsar Completar se enviará el registro con el CV y los datos ingresados.",
                color = White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (d.consentAccepted) {
                        viewModel.submitRegistration(onRegistered)
                    }
                },
                enabled = d.consentAccepted && !state.registerSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent, contentColor = White),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.registerSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Completar", fontWeight = FontWeight.Bold)
                }
            }
        }
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = White)
        }
    }
}
