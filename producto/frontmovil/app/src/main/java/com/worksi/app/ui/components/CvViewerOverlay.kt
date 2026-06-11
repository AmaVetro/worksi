package com.worksi.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.White
import java.io.File

@Composable
fun CvViewerOverlay(
    visible: Boolean,
    loading: Boolean,
    errorMessage: String?,
    pdfBytes: ByteArray?,
    onClose: () -> Unit,
    onDownload: () -> Unit,
    onChangeCv: (() -> Unit)? = null,
    uploadBusy: Boolean = false
) {
    if (!visible) return
    Dialog(
        onDismissRequest = onClose,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false)) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.62f))
                    .padding(16.dp),
            contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)) {
                        IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart)) {
                            Icon(Icons.Filled.Close, contentDescription = null, tint = CyanPrimary)
                        }
                        Text(
                            text = "Tu CV",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary)
                    }
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center) {
                        when {
                            loading ->
                                CircularProgressIndicator(color = CyanPrimary)
                            !errorMessage.isNullOrBlank() ->
                                Text(errorMessage, color = Color(0xFFB00020), fontSize = 15.sp)
                            pdfBytes != null ->
                                PdfScrollViewer(bytes = pdfBytes, modifier = Modifier.fillMaxSize())
                            else -> Text("No hay CV disponible", color = Color.Gray)
                        }
                    }
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onDownload,
                            modifier = Modifier.weight(1f),
                            enabled = !loading && pdfBytes != null && errorMessage == null,
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = CyanPrimary, contentColor = White)) {
                            Text("Descargar CV")
                        }
                        if (onChangeCv != null) {
                            OutlinedButton(
                                onClick = onChangeCv,
                                modifier = Modifier.weight(1f),
                                enabled = !loading && !uploadBusy) {
                                Text(if (uploadBusy) "Subiendo…" else "Cambiar CV")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfScrollViewer(bytes: ByteArray, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val pages =
        remember(bytes) {
            renderPdfPages(context, bytes)
        }
    Column(
        modifier =
            modifier
                .verticalScroll(scrollState)
                .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        pages.forEach { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth)
        }
        if (pages.isEmpty()) {
            Text("No se pudo renderizar el PDF", color = Color.Gray)
        }
    }
}

private fun renderPdfPages(context: Context, bytes: ByteArray): List<Bitmap> {
    if (bytes.isEmpty()) return emptyList()
    val temp = File(context.cacheDir, "cv_preview_${System.currentTimeMillis()}.pdf")
    return try {
        temp.writeBytes(bytes)
        val pfd = ParcelFileDescriptor.open(temp, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(pfd)
        val output = ArrayList<Bitmap>(renderer.pageCount)
        for (i in 0 until renderer.pageCount) {
            renderer.openPage(i).use { page ->
                val width = (page.width * 2).coerceAtLeast(1)
                val height = (page.height * 2).coerceAtLeast(1)
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                output.add(bitmap)
            }
        }
        renderer.close()
        pfd.close()
        output
    } catch (_: Exception) {
        emptyList()
    } finally {
        temp.delete()
    }
}
