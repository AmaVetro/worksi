package com.worksi.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.local.SecureTokenStore
import okhttp3.Headers
import com.worksi.app.data.model.JobOffer
import com.worksi.app.ui.components.CandidateMainBottomBar
import com.worksi.app.ui.components.CandidateSessionSettingsAction
import com.worksi.app.ui.components.MainTab
import com.worksi.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSaved: () -> Unit = {},
    onNavigateToApplications: () -> Unit = {},
    onSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    onOpenJobDetail: (Long) -> Unit = {},
    onOpenApplicationPreview: (Long) -> Unit = {}
) {
    val offer by viewModel.offer.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val empty by viewModel.empty.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val actionBusy by viewModel.actionBusy.collectAsState()
    val showApplyConfirm by viewModel.showApplyConfirm.collectAsState()
    val applySuccess by viewModel.applySuccess.collectAsState()
    val savedJobIds by viewModel.savedJobIds.collectAsState()
    val errText = errorMessage
    val currentOffer = offer
    val feedEnabled = !actionBusy && applySuccess == null

    if (showApplyConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissApplyConfirm() },
            title = { Text("Confirmar postulación") },
            text = { Text("¿Seguro de postular?") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onConfirmApply() },
                    enabled = !actionBusy
                ) {
                    Text("Postular")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissApplyConfirm() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    applySuccess?.let { success ->
        ApplySuccessDialog(
            info = success,
            onViewApplication = {
                viewModel.onDismissApplySuccess()
                onOpenApplicationPreview(success.applicationId)
            },
            onBackToOffers = { viewModel.onDismissApplySuccess() })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", color = White) },
                actions = {
                    CandidateSessionSettingsAction(onLogout = onLogout)
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = CyanPrimary)
            )
        },
        bottomBar = {
            CandidateMainBottomBar(
                selected = MainTab.Offers,
                onProfile = onNavigateToProfile,
                onOffers = { },
                onSaved = onNavigateToSaved,
                onApplications = onNavigateToApplications)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (errText != null) {
                Text(
                    text = errText,
                    color = Color(0xFFB00020),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (currentOffer == null) {
                    Button(onClick = { viewModel.retry() }) {
                        Text("Reintentar")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            when {
                isLoading && currentOffer == null ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanPrimary)
                    }
                empty && currentOffer == null && !isLoading ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay ofertas por ahora.", color = Color.Gray, fontSize = 16.sp)
                    }
                currentOffer != null ->
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.TopStart) {
                        Column(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .verticalScroll(rememberScrollState())) {
                            SwipeableJobOfferCard(
                                offer = currentOffer,
                                enabled = feedEnabled,
                                onSwipeRight = viewModel::onSwipeToApply,
                                onSwipeLeft = viewModel::onSwipeToPass,
                                onVerDetalles = { onOpenJobDetail(currentOffer.id) })
                            Spacer(modifier = Modifier.height(8.dp))
                            ActionButtons(
                                onPostular = viewModel::onSwipeToApply,
                                onPasar = viewModel::onSwipeToPass,
                                onToggleSave = viewModel::onToggleSaveCurrentOffer,
                                isSaved = savedJobIds.contains(currentOffer.id),
                                enabled = feedEnabled)
                        }
                    }
                else ->
                    Spacer(modifier = Modifier.height(0.dp))
            }
        }
    }
}

@Composable
fun ApplySuccessDialog(
    info: ApplySuccessInfo,
    onViewApplication: () -> Unit,
    onBackToOffers: () -> Unit
) {
    Dialog(onDismissRequest = onBackToOffers) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            modifier = Modifier.fillMaxWidth()) {
              Column(
                  modifier = Modifier.padding(24.dp),
                  horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Postulación exitosa",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Has postulado a ${info.title}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier =
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF5F5F5))
                                .padding(14.dp)) {
                          Text(info.company, fontSize = 15.sp, color = Color.Gray)
                          Spacer(modifier = Modifier.height(4.dp))
                          Text(
                              "${info.communeName} · ${info.modality}",
                              fontSize = 14.sp,
                              color = Color.Gray)
                          Spacer(modifier = Modifier.height(4.dp))
                          Text(
                              "$${info.salary}",
                              fontSize = 15.sp,
                              fontWeight = FontWeight.Bold,
                              color = Color.DarkGray)
                          Text(
                              "${info.experienceYears} años de experiencia requerida",
                              fontSize = 13.sp,
                              color = Color.Gray)
                          if (info.matchPercentage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Match: ${info.matchPercentage.toInt()}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyanPrimary)
                          }
                          if (info.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                info.description,
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis)
                          }
                        }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onViewApplication,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = OrangeAccent, contentColor = White),
                        shape = RoundedCornerShape(10.dp)) {
                          Text("Ver postulación", fontWeight = FontWeight.Bold)
                        }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onBackToOffers,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp)) {
                          Text("Volver a Ofertas", fontWeight = FontWeight.SemiBold, color = CyanPrimary)
                        }
                  }
            }
    }
}

@Composable
fun SwipeableJobOfferCard(
    offer: JobOffer,
    enabled: Boolean,
    onSwipeRight: () -> Unit,
    onSwipeLeft: () -> Unit,
    onVerDetalles: () -> Unit
) {
    var offsetX by remember(offer.id) { mutableFloatStateOf(0f) }
    val threshold = 120f
    JobOfferCard(
        offer = offer,
        onVerDetalles = onVerDetalles,
        modifier =
            Modifier.offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(offer.id, enabled) {
                    if (!enabled) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            when {
                                offsetX > threshold -> onSwipeRight()
                                offsetX < -threshold -> onSwipeLeft()
                            }
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount -> offsetX += dragAmount })
                })
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobOfferCard(offer: JobOffer, onVerDetalles: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp).wrapContentHeight()) {
            OfferHeroImage(
                jobId = offer.id,
                externalImageUrl = offer.externalImageUrl,
                hasProtectedJobImage = offer.hasProtectedJobImage,
                modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = offer.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = offer.company,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${offer.communeName} · ${offer.modality}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${offer.salary}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${offer.experienceYears} años exp.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = offer.description,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = onVerDetalles) {
                    Text(
                        "Ver detalles",
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                offer.skills.forEach { skill ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE3F2FD),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text(
                            text = skill,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color(0xFF1565C0),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val pct = offer.matchPercentage
            if (pct != null) {
                Column(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Porcentaje de matching",
                        fontSize = 14.sp,
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LinearProgressIndicator(
                            progress = { pct / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            color = CyanPrimary,
                            trackColor = Color(0xFFE0E0E0),
                        )
                        Text(
                            text = "${pct.toInt()}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SaveOfferButton(
    isSaved: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (isSaved) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanPrimary,
                contentColor = White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = modifier.height(44.dp)
        ) {
            Icon(Icons.Filled.Bookmark, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Guardada", color = White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            border = BorderStroke(1.dp, CyanPrimary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = modifier.height(44.dp)
        ) {
            Icon(Icons.Filled.Bookmark, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Guardar", color = CyanPrimary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun ActionButtons(
    onPostular: () -> Unit,
    onPasar: () -> Unit,
    onToggleSave: () -> Unit,
    isSaved: Boolean,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        Button(
            onClick = onPostular,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = OrangeAccent,
                contentColor = White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
        ) {
            Icon(Icons.Filled.Send, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Postular", color = White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        SaveOfferButton(
            isSaved = isSaved,
            onClick = onToggleSave,
            enabled = enabled,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onPasar,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F),
                contentColor = White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
        ) {
            Icon(Icons.Filled.Close, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Pasar", color = White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun OfferHeroImage(
    jobId: Long,
    externalImageUrl: String?,
    hasProtectedJobImage: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val url: String? =
        when {
            !externalImageUrl.isNullOrBlank() -> externalImageUrl
            hasProtectedJobImage -> RetrofitClient.candidateJobImageUrl(jobId)
            else -> null
        }
    val imageRequest =
        remember(context, url, hasProtectedJobImage, jobId) {
            if (url == null) {
                null
            } else {
                val b = ImageRequest.Builder(context).data(url).crossfade(true)
                if (hasProtectedJobImage) {
                    val t = SecureTokenStore.getAccessToken()
                    if (!t.isNullOrBlank()) {
                        b.headers(Headers.headersOf("Authorization", "Bearer $t"))
                    }
                }
                b.build()
            }
        }
    BoxWithConstraints(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFFE0F7FA)),
        contentAlignment = Alignment.Center) {
        val byWidth = maxWidth * 9f / 16f
        val h = if (constraints.hasBoundedHeight) minOf(byWidth, maxHeight) else byWidth
        Box(
            Modifier.fillMaxWidth().height(h),
            contentAlignment = Alignment.Center) {
            if (imageRequest != null) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    Icons.Filled.Business,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(48.dp))
            }
        }
    }
}