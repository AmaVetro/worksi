package com.worksi.app.ui.applications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.worksi.app.data.model.CandidateApplicationListItemJson
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.White

private val PageBackground = Color(0xFFF5F5F5)
private val MutedText = Color(0xFF757575)

private fun statusLabel(status: String): String =
    when (status) {
      "VIEWED" -> "Vista por empresa"
      "APPLIED" -> "Postulada"
      else -> status
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsScreen(
    viewModel: ApplicationsViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onOpenPreview: (Long) -> Unit
) {
  val isLoading by viewModel.isLoading.collectAsState()
  val error by viewModel.errorMessage.collectAsState()
  val items by viewModel.items.collectAsState()

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Mis Postulaciones", color = White) },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = CyanPrimary))
      },
      bottomBar = {
        NavigationBar(containerColor = CyanPrimary) {
          NavigationBarItem(
              selected = false,
              onClick = onNavigateToProfile,
              icon = { Icon(Icons.Filled.Person, contentDescription = null, tint = White) },
              label = { Text("Perfil", color = White, fontSize = 12.sp) },
              colors = navColors())
          NavigationBarItem(
              selected = false,
              onClick = onNavigateToHome,
              icon = { Icon(Icons.Filled.Home, contentDescription = null, tint = White) },
              label = { Text("Ofertas", color = White, fontSize = 12.sp) },
              colors = navColors())
          NavigationBarItem(
              selected = true,
              onClick = { },
              icon = { Icon(Icons.Filled.Menu, contentDescription = null, tint = White) },
              label = { Text("Postulaciones", color = White, fontSize = 12.sp) },
              colors = navColors())
        }
      }) { innerPadding ->
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(PageBackground)) {
              when {
                isLoading ->
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center), color = CyanPrimary)
                error != null -> {
                  Column(
                      modifier = Modifier.align(Alignment.Center).padding(24.dp),
                      horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error ?: "", color = Color(0xFFB00020))
                        Button(
                            onClick = { viewModel.retry() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            modifier = Modifier.padding(top = 12.dp)) {
                              Text("Reintentar")
                            }
                      }
                }
                items.isEmpty() ->
                    Text(
                        "Aún no tienes postulaciones.",
                        modifier = Modifier.align(Alignment.Center),
                        color = MutedText)
                else ->
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                          items(items, key = { it.applicationId }) { item ->
                            ApplicationListCard(item = item, onClick = { onOpenPreview(item.applicationId) })
                          }
                        }
              }
            }
      }
}

@Composable
private fun ApplicationListCard(item: CandidateApplicationListItemJson, onClick: () -> Unit) {
  Card(
      modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(item.jobTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Text(item.companyCommercialName, color = MutedText, fontSize = 14.sp)
          Text("Sueldo: $${item.salaryOffered}", color = MutedText, fontSize = 13.sp)
          Text(
              "Estado: ${statusLabel(item.status)}",
              modifier = Modifier.padding(top = 6.dp),
              color = CyanPrimary,
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp)
        }
      }
}

@Composable
private fun navColors() =
    NavigationBarItemDefaults.colors(
        unselectedIconColor = White,
        unselectedTextColor = White,
        selectedIconColor = White,
        selectedTextColor = White,
        indicatorColor = CyanPrimary)
