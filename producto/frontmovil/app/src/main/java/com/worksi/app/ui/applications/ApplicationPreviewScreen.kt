package com.worksi.app.ui.applications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.worksi.app.ui.components.MatchScoreRow
import com.worksi.app.ui.components.modalityLabel
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.White

private val PageBackground = Color(0xFFF5F5F5)
private val MutedText = Color(0xFF757575)
private val DetailSpacing = 12.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationPreviewScreen(
    applicationId: Long,
    onBack: () -> Unit,
    onGoToJob: (Long) -> Unit,
    viewModel: ApplicationPreviewViewModel =
        viewModel(factory = ApplicationPreviewViewModel.factory(applicationId))
) {
  val isLoading by viewModel.isLoading.collectAsState()
  val error by viewModel.errorMessage.collectAsState()
  val detail by viewModel.detail.collectAsState()

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Vista Previa Oferta", color = White) },
            navigationIcon = {
              IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = White)
              }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = CyanPrimary))
      }) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(PageBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)) {
              when {
                isLoading -> CircularProgressIndicator(color = CyanPrimary)
                error != null -> {
                  Text(error ?: "", color = Color(0xFFB00020))
                  Button(
                      onClick = { viewModel.retry() },
                      colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                      modifier = Modifier.padding(top = 12.dp)) {
                        Text("Reintentar")
                      }
                }
                detail != null -> {
                  val d = detail!!
                  Card(
                      shape = RoundedCornerShape(12.dp),
                      colors = CardDefaults.cardColors(containerColor = White),
                      modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                          Text(d.jobTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                          Spacer(modifier = Modifier.height(DetailSpacing))
                          Text(d.companyCommercialName, color = MutedText)
                          Spacer(modifier = Modifier.height(DetailSpacing))
                          Text("Sueldo: $${d.salaryOffered}", color = MutedText)
                          Spacer(modifier = Modifier.height(DetailSpacing))
                          Text("Comuna: ${d.communeName}", color = MutedText)
                          Spacer(modifier = Modifier.height(DetailSpacing))
                          Text(
                              "Modalidad: ${modalityLabel(d.modality)}",
                              color = MutedText)
                          Spacer(modifier = Modifier.height(DetailSpacing))
                          Text(
                              "Exp. requerida: ${d.yearsExperienceRequired} años",
                              color = MutedText)
                          Spacer(modifier = Modifier.height(DetailSpacing))
                          MatchScoreRow(
                              score = d.matchScore,
                              barWidth = 120.dp,
                              labelColor = CyanPrimary)
                          if (d.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(d.description, color = MutedText, fontSize = 14.sp)
                          }
                        }
                      }
                  Spacer(modifier = Modifier.height(16.dp))
                  Button(
                      onClick = { onGoToJob(d.jobId) },
                      modifier = Modifier.fillMaxWidth(),
                      colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)) {
                        Text("Ir a Oferta")
                      }
                }
              }
            }
      }
}
