package com.worksi.app.ui.applications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.worksi.app.ui.components.CandidateMainBottomBar
import com.worksi.app.ui.components.CandidateOfferListCard
import com.worksi.app.ui.components.CandidateTopSessionActions
import com.worksi.app.ui.components.MainTab
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.White

private val PageBackground = Color(0xFFF5F5F5)
private val MutedText = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsScreen(
    viewModel: ApplicationsViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSaved: () -> Unit = {},
    onNavigateToMatches: () -> Unit = {},
    onOpenPreview: (Long) -> Unit,
    onLogout: () -> Unit
) {
  val isLoading by viewModel.isLoading.collectAsState()
  val error by viewModel.errorMessage.collectAsState()
  val items by viewModel.items.collectAsState()

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Mis Postulaciones", color = White) },
            actions = {
              CandidateTopSessionActions(
                  onNavigateToMatches = onNavigateToMatches, onLogout = onLogout)
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = CyanPrimary))
      },
      bottomBar = {
        CandidateMainBottomBar(
            selected = MainTab.Applications,
            onProfile = onNavigateToProfile,
            onOffers = onNavigateToHome,
            onSaved = onNavigateToSaved,
            onApplications = { })
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
                            CandidateOfferListCard(
                                title = item.jobTitle,
                                company = item.companyCommercialName,
                                salary = item.salaryOffered,
                                matchPercentage = item.matchScore?.toFloat(),
                                onClick = { onOpenPreview(item.applicationId) })
                          }
                        }
              }
            }
      }
}
