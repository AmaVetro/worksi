package com.worksi.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.local.SecureTokenStore
import okhttp3.Headers
import com.worksi.app.data.model.JobOffer
import com.worksi.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMenu: () -> Unit = {},
    onSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    onOpenJobDetail: (Long) -> Unit = {}
) {
    val offer by viewModel.offer.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val empty by viewModel.empty.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val actionBusy by viewModel.actionBusy.collectAsState()
    val errText = errorMessage
    val currentOffer = offer
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", color = White) },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Configuración", tint = White)
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    showMenu = false
                                    onLogout()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = CyanPrimary)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = CyanPrimary) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = White) },
                    label = { Text("Perfil", color = White, fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = White,
                        unselectedTextColor = White,
                        selectedIconColor = White,
                        selectedTextColor = White,
                        indicatorColor = CyanPrimary
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = White) },
                    label = { Text("Home", color = White, fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = White,
                        unselectedTextColor = White,
                        selectedIconColor = White,
                        selectedTextColor = White,
                        indicatorColor = CyanPrimary
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMenu,
                    icon = { Icon(Icons.Filled.Menu, contentDescription = "Menú principal", tint = White) },
                    label = { Text("Menú", color = White, fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = White,
                        unselectedTextColor = White,
                        selectedIconColor = White,
                        selectedTextColor = White,
                        indicatorColor = CyanPrimary
                    )
                )
            }
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
                            JobOfferCard(currentOffer, onVerDetalles = { onOpenJobDetail(currentOffer.id) })
                            Spacer(modifier = Modifier.height(8.dp))
                            ActionButtons(
                                onPostular = viewModel::onPostular,
                                onGuardar = viewModel::onGuardar,
                                onPasar = viewModel::onPasar,
                                enabled = !actionBusy)
                        }
                    }
                else ->
                    Spacer(modifier = Modifier.height(0.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobOfferCard(offer: JobOffer, onVerDetalles: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
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
fun ActionButtons(
    onPostular: () -> Unit,
    onGuardar: () -> Unit,
    onPasar: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
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
                .padding(horizontal = 2.dp)
        ) {
            Icon(Icons.Filled.Send, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Postular", color = White, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        Button(
            onClick = onGuardar,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanPrimary,
                contentColor = White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .padding(horizontal = 2.dp)
        ) {
            Icon(Icons.Filled.Bookmark, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Guardar", color = White, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

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
                .padding(horizontal = 2.dp)
        ) {
            Icon(Icons.Filled.Close, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Pasar", color = White, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
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