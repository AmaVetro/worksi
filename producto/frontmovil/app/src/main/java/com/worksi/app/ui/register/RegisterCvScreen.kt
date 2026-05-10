package com.worksi.app.ui.register

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White

private const val MAX_CV_BYTES = 1_000_000L

private val RegisterBackButtonRowHeight = 56.dp

@Composable
fun RegisterCvScreen(
    viewModel: CandidateRegisterViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val state by viewModel.ui.collectAsState()
    val d = state.draft
    val context = LocalContext.current
    var localError by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        localError = null
        val resolver = context.contentResolver
        val mime = resolver.getType(uri)
        if (mime != null && mime != "application/pdf") {
            localError = "Solo se permite PDF"
            return@rememberLauncherForActivityResult
        }
        val size = resolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: 0L
        if (size <= 0L) {
            localError = "No se pudo leer el archivo"
            return@rememberLauncherForActivityResult
        }
        if (size > MAX_CV_BYTES) {
            localError = "El PDF supera 1 MB"
            return@rememberLauncherForActivityResult
        }
        var display: String? = null
        resolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { c ->
                if (c.moveToFirst()) {
                    val idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) display = c.getString(idx)
                }
            }
        viewModel.setCv(uri.toString(), display)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyanPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, top = RegisterBackButtonRowHeight, end = 24.dp, bottom = 24.dp)
        ) {
            Text("Tu CV", style = MaterialTheme.typography.headlineSmall, color = White)
            Spacer(Modifier.height(16.dp))
            Text(
                "Sube un PDF de hasta 1 MB. El archivo se guarda solo en este dispositivo hasta que aceptes Uso de datos.",
                color = White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = { launcher.launch(arrayOf("application/pdf")) },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, White),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Elegir PDF")
            }
            Spacer(Modifier.height(12.dp))
            if (d.cvUri != null) {
                Text("Archivo: ${d.cvDisplayName ?: "CV.pdf"}", color = White)
                OutlinedButton(
                    onClick = { viewModel.clearCv(); localError = null },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, White),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Quitar")
                }
            }
            localError?.let { Text(it, color = OrangeAccent) }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    localError = if (d.cvUri == null) "Debes seleccionar un PDF" else null
                    if (d.cvUri != null) onNext()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent, contentColor = White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Siguiente", fontWeight = FontWeight.Bold)
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
