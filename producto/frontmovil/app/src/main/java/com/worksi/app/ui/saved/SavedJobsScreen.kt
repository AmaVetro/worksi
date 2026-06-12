package com.worksi.app.ui.saved

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.worksi.app.ui.components.CandidateMainBottomBar
import com.worksi.app.ui.components.CandidateOfferListCard
import com.worksi.app.ui.components.CandidateSessionSettingsAction
import com.worksi.app.ui.components.MainTab
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.White

private val PageBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedJobsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToApplications: () -> Unit,
    onOpenJobDetail: (Long) -> Unit,
    onLogout: () -> Unit,
    viewModel: SavedJobsViewModel = viewModel()
) {
    val state by viewModel.ui.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guardadas", color = White) },
                actions = { CandidateSessionSettingsAction(onLogout = onLogout) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = CyanPrimary))
        },
        bottomBar = {
            CandidateMainBottomBar(
                selected = MainTab.Saved,
                onProfile = onNavigateToProfile,
                onOffers = onNavigateToHome,
                onSaved = {},
                onApplications = onNavigateToApplications)
        }) { padding ->
          Column(
              modifier =
                  Modifier.fillMaxSize()
                      .padding(padding)
                      .background(PageBackground)
                      .padding(16.dp)
                      .verticalScroll(rememberScrollState())) {
              when {
                  state.loading ->
                      Box(
                          modifier = Modifier.fillMaxWidth().height(200.dp),
                          contentAlignment = Alignment.Center) {
                          CircularProgressIndicator(color = CyanPrimary)
                      }
                  state.errorMessage != null -> {
                      Text(state.errorMessage ?: "", color = Color(0xFFB00020))
                      Spacer(Modifier.height(8.dp))
                      Button(onClick = { viewModel.reload() }) { Text("Reintentar") }
                  }
                  state.items.isEmpty() ->
                      Text(
                          "No tienes ofertas guardadas.",
                          color = Color.Gray,
                          fontSize = 16.sp,
                          modifier = Modifier.padding(top = 24.dp))
                  else ->
                      state.items.forEach { offer ->
                          CandidateOfferListCard(
                              title = offer.title,
                              company = offer.company,
                              salary = offer.salary,
                              matchPercentage = offer.matchPercentage,
                              modifier = Modifier.padding(bottom = 12.dp),
                              bottomContent = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { onOpenJobDetail(offer.id) },
                                        modifier = Modifier.weight(1f)) {
                                        Text("Ver oferta")
                                    }
                                    OutlinedButton(
                                        onClick = { viewModel.unsave(offer.id) },
                                        enabled = state.busyJobId != offer.id,
                                        modifier = Modifier.weight(1f)) {
                                        Text("Quitar")
                                    }
                                }
                              })
                      }
              }
          }
        }
}
