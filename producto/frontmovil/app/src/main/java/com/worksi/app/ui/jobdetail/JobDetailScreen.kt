package com.worksi.app.ui.jobdetail

import androidx.compose.foundation.background
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.worksi.app.data.model.CandidateJobDetailJson
import com.worksi.app.data.model.CandidateJobDetailMatchJson
import com.worksi.app.ui.components.MatchBreakdownContent
import com.worksi.app.ui.components.modalityLabel
import com.worksi.app.ui.components.workloadLabel
import com.worksi.app.ui.home.OfferHeroImage
import com.worksi.app.ui.home.SaveOfferButton
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JobDetailScreen(
    jobId: Long,
    onBack: () -> Unit,
    viewModel: JobDetailViewModel =
        viewModel(factory = JobDetailViewModel.factory(jobId), key = "job_detail_$jobId")
) {
  val state by viewModel.state.collectAsState()
  val isSaved by viewModel.isSaved.collectAsState()
  val saveError by viewModel.saveError.collectAsState()
  val saveBusy by viewModel.saveBusy.collectAsState()

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Text(
                  when (val s = state) {
                    is JobDetailState.Ready -> s.detail.title
                    else -> "Detalle de la oferta"
                  },
                  color = White,
                  maxLines = 1)
            },
            navigationIcon = {
              IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = White)
              }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = CyanPrimary))
      }) { innerPadding ->
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp)) {
              when (val s = state) {
                JobDetailState.Loading ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                      CircularProgressIndicator(color = CyanPrimary)
                    }
                is JobDetailState.Error ->
                    Column(
                        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(text = s.message, color = Color(0xFFB00020), fontSize = 15.sp)
                      Spacer(modifier = Modifier.height(16.dp))
                      Button(onClick = { viewModel.retry() }) { Text("Reintentar") }
                    }
                is JobDetailState.Ready ->
                    JobDetailContent(
                        d = s.detail,
                        isSaved = isSaved,
                        saveError = saveError,
                        saveBusy = saveBusy,
                        onToggleSave = { viewModel.toggleSaveJob() })
              }
            }
      }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun JobDetailContent(
    d: CandidateJobDetailJson,
    isSaved: Boolean,
    saveError: String?,
    saveBusy: Boolean,
    onToggleSave: () -> Unit
) {
  Column(
      modifier =
          Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 24.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
          Column(modifier = Modifier.padding(12.dp)) {
            OfferHeroImage(
                jobId = d.jobId,
                externalImageUrl = d.externalImageUrl,
                hasProtectedJobImage = d.hasProtectedJobImage,
                modifier = Modifier.fillMaxWidth().height(140.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = d.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = d.companyName, fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${d.communeName.ifBlank { "—" }} · ${modalityLabel(d.modality)}",
                fontSize = 15.sp,
                color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Jornada: ${workloadLabel(d.workload)}",
                fontSize = 15.sp,
                color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$${d.salaryOffered}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${d.yearsExperienceRequired} años de experiencia requeridos",
                fontSize = 14.sp,
                color = Color.Gray)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Descripción",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = d.description, fontSize = 15.sp, color = Color.Black)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Skills requeridas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  d.skills.forEach { sk ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE3F2FD),
                        border = ButtonDefaults.outlinedButtonBorder) {
                          Text(
                              text = sk.name,
                              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                              color = Color(0xFF1565C0),
                              fontSize = 13.sp,
                              fontWeight = FontWeight.Medium)
                        }
                  }
                }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
          JobMatchingCard(match = d.match)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (saveError != null) {
          Text(
              text = saveError,
              color = Color(0xFFB00020),
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.fillMaxWidth())
          Spacer(modifier = Modifier.height(8.dp))
        }

        SaveOfferButton(
            isSaved = isSaved,
            onClick = onToggleSave,
            enabled = !saveBusy,
            modifier = Modifier.fillMaxWidth())
      }
}

@Composable
private fun JobMatchingCard(match: CandidateJobDetailMatchJson?) {
  var showBreakdown by remember { mutableStateOf(false) }
  val score = match?.score?.toFloat()?.coerceIn(0f, 100f)
  val explanation = match?.explanation?.trim().orEmpty()

  Column(modifier = Modifier.padding(12.dp)) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
          Text(
              text = "Matching con tu CV (IA)",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = CyanPrimary,
              modifier = Modifier.weight(1f))
          TextButton(
              onClick = { showBreakdown = !showBreakdown },
              contentPadding = PaddingValues(0.dp)) {
                Text(
                    text = if (showBreakdown) "Volver" else "Ver detalles",
                    color = CyanPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium)
              }
        }
    Spacer(modifier = Modifier.height(10.dp))
    AnimatedContent(
        targetState = showBreakdown,
        transitionSpec = {
          fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(350))
        },
        label = "matching_card_content") { breakdownMode ->
          if (!breakdownMode) {
            Column(modifier = Modifier.fillMaxWidth()) {
              if (score != null) {
                Text(
                    text = "Porcentaje estimado de compatibilidad",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center) {
                      LinearProgressIndicator(
                          progress = { score / 100f },
                          modifier =
                              Modifier.fillMaxWidth()
                                  .height(24.dp)
                                  .clip(RoundedCornerShape(12.dp)),
                          color = CyanPrimary,
                          trackColor = Color(0xFFE0E0E0),
                      )
                      Text(
                          text = "${score.toInt()}%",
                          fontSize = 16.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White)
                    }
                Spacer(modifier = Modifier.height(12.dp))
              }
              Text(
                  text = "Por qué este porcentaje",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color.DarkGray)
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                  text =
                      if (explanation.isNotEmpty()) {
                        explanation
                      } else {
                        "No hay una explicación detallada disponible para esta oferta."
                      },
                  fontSize = 15.sp,
                  color = Color.Black,
                  lineHeight = 22.sp)
            }
          } else {
            MatchBreakdownContent(
                breakdown = match?.matchBreakdown,
                fallbackScore = match?.score)
          }
        }
  }
}
