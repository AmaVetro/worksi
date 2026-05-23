package com.worksi.app.ui.register

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White
import com.worksi.app.ui.theme.worksiRegisterSecondaryButtonColors
import com.worksi.app.validation.PdfCvTextRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    var showValidationErrors by remember { mutableStateOf(false) }
    var isCvChecking by remember { mutableStateOf(false) }
    var pickValidating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val busy = isCvChecking || pickValidating

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        localError = null
        val resolver = context.contentResolver
        val mime = resolver.getType(uri)
        if (mime != null && mime != "application/pdf") {
            localError = "Solo se permite PDF"
            showValidationErrors = true
            return@rememberLauncherForActivityResult
        }
        val size = resolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: 0L
        if (size <= 0L) {
            localError = "No se pudo leer el archivo"
            showValidationErrors = true
            return@rememberLauncherForActivityResult
        }
        if (size > MAX_CV_BYTES) {
            localError = "El PDF supera 1 MB"
            showValidationErrors = true
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
        scope.launch {
            pickValidating = true
            val err =
                withContext(Dispatchers.IO) {
                    val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                    if (bytes == null) {
                        "No se pudo leer el archivo"
                    } else {
                        PdfCvTextRules.validatePdfBytes(bytes)
                    }
                }
            pickValidating = false
            if (err != null) {
                localError = err
                showValidationErrors = true
            } else {
                viewModel.setCv(uri.toString(), display)
                showValidationErrors = false
            }
        }
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
                "Sube un PDF de hasta 1 MB con texto seleccionable (no escaneo solo imagen). El archivo queda solo en este dispositivo hasta Uso de datos. Antes de avanzar se comprueba el texto del PDF.",
                color = White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { launcher.launch(arrayOf("application/pdf")) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !busy,
                colors = worksiRegisterSecondaryButtonColors(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Elegir PDF")
            }
            Spacer(Modifier.height(12.dp))
            if (d.cvUri != null) {
                Text("Archivo: ${d.cvDisplayName ?: "CV.pdf"}", color = White)
                Button(
                    onClick = { viewModel.clearCv(); localError = null },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !busy,
                    colors = worksiRegisterSecondaryButtonColors(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Quitar")
                }
            }
            if (busy) {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text =
                            if (pickValidating) {
                                "Comprobando que el PDF tenga texto seleccionable…"
                            } else {
                                "Comprobando PDF antes de continuar…"
                            },
                        color = White.copy(alpha = 0.95f),
                        fontSize = 14.sp
                    )
                }
            }
            localError?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = OrangeAccent, fontSize = 14.sp)
            }
            Spacer(Modifier.height(24.dp))
            RegisterRequiredFieldsHint(
                showValidationErrors && (d.cvUri == null || localError != null)
            )
            Button(
                onClick = {
                    showValidationErrors = true
                    if (d.cvUri == null) {
                        localError = "Debes seleccionar un PDF"
                        return@Button
                    }
                    scope.launch {
                        localError = null
                        isCvChecking = true
                        val err = viewModel.validateCvPdfSelectable(d.cvUri!!)
                        isCvChecking = false
                        if (err != null) {
                            localError = err
                        } else {
                            showValidationErrors = false
                            onNext()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                enabled = !busy,
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
