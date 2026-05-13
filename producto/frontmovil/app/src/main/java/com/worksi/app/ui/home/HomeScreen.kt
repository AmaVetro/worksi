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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.worksi.app.data.model.JobOffer
import com.worksi.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMenu: () -> Unit = {},
    onSettings: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val offer by viewModel.offer.collectAsState()
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
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            JobOfferCard(offer)
            Spacer(modifier = Modifier.height(8.dp))
            ActionButtons(
                onPostular = viewModel::onPostular,
                onGuardar = viewModel::onGuardar,
                onPasar = viewModel::onPasar
            )
        }
    }
}

@Composable
fun JobOfferCard(offer: JobOffer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Imagen proporcional (16:9)
            AsyncImage(
                model = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8N3x8aW5mb3JtYXRpY2F8ZW58MHx8MHx8fDA%3D",
                contentDescription = "Logo de la empresa",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Título de la oferta
            Text(
                text = offer.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fila principal: izquierda (empresa + ubicación) | derecha (renta + años)
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

            // Descripción
            Text(
                text = offer.description,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Ver detalles centrado y subrayado
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = { /* TODO: ver detalles */ }) {
                    Text(
                        "Ver detalles",
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Skills centradas
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                offer.skills.forEachIndexed { index, skill ->
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
                    if (index != offer.skills.lastIndex) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Porcentaje de matching con número dentro de la barra
            Column(
                modifier = Modifier.fillMaxWidth(),
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
                        progress = { offer.matchPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = CyanPrimary,
                        trackColor = Color(0xFFE0E0E0),
                    )
                    Text(
                        text = "${offer.matchPercentage.toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButtons(onPostular: () -> Unit, onGuardar: () -> Unit, onPasar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onPostular,
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