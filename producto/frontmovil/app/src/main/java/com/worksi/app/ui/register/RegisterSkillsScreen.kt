package com.worksi.app.ui.register

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White
import com.worksi.app.ui.theme.worksiOnCyanFilterChipColors

private val RegisterBackButtonRowHeight = 56.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterSkillsScreen(
    viewModel: CandidateRegisterViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val state by viewModel.ui.collectAsState()
    val d = state.draft
    var sectorMenu by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }
    val chipColors = worksiOnCyanFilterChipColors()

    LaunchedEffect(Unit) {
        viewModel.loadSectors()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyanPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, top = RegisterBackButtonRowHeight, end = 24.dp, bottom = 24.dp)
        ) {
            Text("Rubro y skills", style = MaterialTheme.typography.headlineSmall, color = White)
            Spacer(Modifier.height(8.dp))
            Text("Elige entre 3 y 12 habilidades del rubro seleccionado.", color = White.copy(alpha = 0.9f))
            Spacer(Modifier.height(16.dp))

            if (state.catalogLoading && state.sectors.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = White
                )
            }
            state.catalogError?.let { Text(it, color = OrangeAccent) }

            Text("Rubro (*)", color = White.copy(alpha = 0.9f))
            ExposedDropdownMenuBox(
                expanded = sectorMenu,
                onExpandedChange = { sectorMenu = !sectorMenu }
            ) {
                RegisterOutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = state.sectors.find { it.id == d.sectorId }?.name ?: "",
                    onValueChange = {},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sectorMenu) },
                    isError = showErrors && d.sectorId == null,
                    label = { Text("Sector") }
                )
                DropdownMenu(
                    expanded = sectorMenu,
                    onDismissRequest = { sectorMenu = false }
                ) {
                    state.sectors.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s.name) },
                            onClick = {
                                viewModel.onSectorSelected(s.id)
                                sectorMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            if (state.catalogLoading && d.sectorId != null && state.skillsForSector.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = White
                )
            }
            Text("Skills seleccionadas: ${d.skillIds.size} / 12 (minimo 3)", color = White.copy(alpha = 0.9f))
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                state.skillsForSector.forEach { sk ->
                    FilterChip(
                        selected = d.skillIds.contains(sk.id),
                        onClick = { viewModel.toggleSkill(sk.id) },
                        label = { Text(sk.name) },
                        colors = chipColors
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            RegisterStepErrorHints(d.skillsStepValidationMessages(showErrors))
            Button(
                onClick = {
                    showErrors = true
                    viewModel.clearCatalogError()
                    if (d.isSkillsStepValid()) onNext()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent, contentColor = White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Siguiente", fontWeight = FontWeight.Bold)
            }
        }
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = White)
        }
    }
}

private fun RegisterDraft.isSkillsStepValid(): Boolean {
    val n = skillIds.size
    return sectorId != null && n in 3..12
}

private fun RegisterDraft.skillsStepValidationMessages(showErrors: Boolean): List<String> {
    if (!showErrors || isSkillsStepValid()) return emptyList()
    val messages = mutableListOf<String>()
    if (sectorId == null) {
        messages.add("Selecciona un rubro.")
    }
    val n = skillIds.size
    when {
        n < 3 -> messages.add("Selecciona entre 3 y 12 skills.")
        n > 12 -> messages.add("Puedes seleccionar como maximo 12 skills.")
    }
    return messages
}
