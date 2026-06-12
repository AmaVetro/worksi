package com.worksi.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White
import com.worksi.app.ui.theme.worksiOnCyanFilterChipColors
import kotlin.math.roundToInt

private val MODALITIES =
    listOf("REMOTE" to "Remoto", "HYBRID" to "Híbrido", "ONSITE" to "Presencial")
private val WORKLOADS =
    listOf("FULL_TIME" to "Full time", "PART_TIME" to "Part time", "OTHER" to "Otro")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileEditScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ProfileEditViewModel = viewModel()
) {
    val state by viewModel.ui.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyanPrimary))
        }) { padding ->
          when {
              state.loading ->
                  Box(
                      modifier = Modifier.fillMaxSize().padding(padding),
                      contentAlignment = Alignment.Center) {
                      CircularProgressIndicator(color = CyanPrimary)
                  }
              else ->
                  Column(
                      modifier =
                          Modifier.fillMaxSize()
                              .padding(padding)
                              .padding(16.dp)
                              .verticalScroll(rememberScrollState())) {
                      if (state.errorMessage != null) {
                          Text(state.errorMessage ?: "", color = Color(0xFFB00020), fontSize = 14.sp)
                          Spacer(Modifier.height(8.dp))
                      }
                      OutlinedTextField(
                          value = state.profileSummary,
                          onValueChange = viewModel::setProfileSummary,
                          label = { Text("Descripción personal") },
                          modifier = Modifier.fillMaxWidth(),
                          minLines = 3)
                      Spacer(Modifier.height(12.dp))
                      Text("Región", fontWeight = FontWeight.SemiBold)
                      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                          state.regions.forEach { r ->
                              FilterChip(
                                  selected = state.regionId == r.id,
                                  onClick = { viewModel.setRegion(r.id) },
                                  label = { Text(r.name) },
                                  colors = worksiOnCyanFilterChipColors())
                          }
                      }
                      Spacer(Modifier.height(12.dp))
                      Text("Comuna", fontWeight = FontWeight.SemiBold)
                      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                          state.communes.forEach { c ->
                              FilterChip(
                                  selected = state.communeId == c.id,
                                  onClick = { viewModel.setCommune(c.id) },
                                  label = { Text(c.name) },
                                  colors = worksiOnCyanFilterChipColors())
                          }
                      }
                      Spacer(Modifier.height(12.dp))
                      Text("Rubro", fontWeight = FontWeight.SemiBold)
                      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                          state.sectors.forEach { s ->
                              FilterChip(
                                  selected = state.sectorId == s.id,
                                  onClick = { viewModel.setSector(s.id) },
                                  label = { Text(s.name) },
                                  colors = worksiOnCyanFilterChipColors())
                          }
                      }
                      Spacer(Modifier.height(12.dp))
                      Text("Sueldo esperado (mín)", fontWeight = FontWeight.SemiBold)
                      Slider(
                          value = state.salaryMin,
                          onValueChange = { v ->
                              viewModel.setSalaryRange(v.coerceAtMost(state.salaryMax), state.salaryMax)
                          },
                          valueRange = 300_000f..8_000_000f)
                      Text("$${state.salaryMin.roundToInt()}", fontSize = 14.sp)
                      Text("Sueldo esperado (máx)", fontWeight = FontWeight.SemiBold)
                      Slider(
                          value = state.salaryMax,
                          onValueChange = { v ->
                              viewModel.setSalaryRange(state.salaryMin, v.coerceAtLeast(state.salaryMin))
                          },
                          valueRange = 300_000f..8_000_000f)
                      Text("$${state.salaryMax.roundToInt()}", fontSize = 14.sp)
                      Spacer(Modifier.height(8.dp))
                      Text("Años de experiencia: ${state.yearsExperience}", fontWeight = FontWeight.SemiBold)
                      Slider(
                          value = state.yearsExperience.toFloat(),
                          onValueChange = { viewModel.setYearsExperience(it.roundToInt()) },
                          valueRange = 0f..50f,
                          steps = 49)
                      Spacer(Modifier.height(12.dp))
                      Text("Modalidades preferidas", fontWeight = FontWeight.SemiBold)
                      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                          MODALITIES.forEach { (code, label) ->
                              FilterChip(
                                  selected = state.selectedModalities.contains(code),
                                  onClick = { viewModel.toggleModality(code) },
                                  label = { Text(label) },
                                  colors = worksiOnCyanFilterChipColors())
                          }
                      }
                      Spacer(Modifier.height(12.dp))
                      Text("Cargas horarias preferidas", fontWeight = FontWeight.SemiBold)
                      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                          WORKLOADS.forEach { (code, label) ->
                              FilterChip(
                                  selected = state.selectedWorkloads.contains(code),
                                  onClick = { viewModel.toggleWorkload(code) },
                                  label = { Text(label) },
                                  colors = worksiOnCyanFilterChipColors())
                          }
                      }
                      Spacer(Modifier.height(12.dp))
                      Text(
                          "Skills (${state.selectedSkillIds.size} seleccionadas, mín. 3 máx. 12)",
                          fontWeight = FontWeight.SemiBold)
                      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                          state.skills.forEach { sk ->
                              FilterChip(
                                  selected = state.selectedSkillIds.contains(sk.id),
                                  onClick = { viewModel.toggleSkill(sk.id) },
                                  label = { Text(sk.name) },
                                  colors = worksiOnCyanFilterChipColors())
                          }
                      }
                      Spacer(Modifier.height(24.dp))
                      Button(
                          onClick = { viewModel.save(onSaved) },
                          enabled = !state.saving,
                          modifier = Modifier.fillMaxWidth(),
                          colors =
                              androidx.compose.material3.ButtonDefaults.buttonColors(
                                  containerColor = OrangeAccent)) {
                          if (state.saving) {
                              CircularProgressIndicator(color = White, modifier = Modifier.height(20.dp))
                          } else {
                              Text("Guardar cambios", color = White, fontWeight = FontWeight.Bold)
                          }
                      }
                  }
          }
        }
}
