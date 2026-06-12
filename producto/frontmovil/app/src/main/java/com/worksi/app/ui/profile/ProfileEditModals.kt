package com.worksi.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.worksi.app.data.model.CandidateProfilePatchJson
import com.worksi.app.data.model.CatalogItemDto
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

private const val SALARY_STEP = 50_000f
private val SALARY_RANGE_F = 300_000f..8_000_000f
private val SALARY_SLIDER_DEFAULT = 1_000_000f..2_500_000f
private const val MAX_YEARS_EXPERIENCE = 50

private val MODALITIES =
    listOf("REMOTE" to "Remoto", "HYBRID" to "Híbrido", "ONSITE" to "Presencial")
private val WORKLOADS =
    listOf("FULL_TIME" to "Full time", "PART_TIME" to "Part time", "OTHER" to "Otro")

private fun snapSalary(value: Float): Float {
    val stepped = (value / SALARY_STEP).roundToInt() * SALARY_STEP
    return stepped.coerceIn(SALARY_RANGE_F.start, SALARY_RANGE_F.endInclusive)
}

private fun formatClPesos(amount: Long): String {
    val fmt = NumberFormat.getNumberInstance(Locale("es", "CL"))
    return "$${fmt.format(amount)}"
}

private fun parseDisplayName(full: String): Triple<String, String, String> {
    val tokens = full.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        tokens.isEmpty() -> Triple("", "", "")
        tokens.size == 1 -> Triple(tokens[0], tokens[0], tokens[0])
        tokens.size == 2 -> Triple(tokens[0], tokens[1], tokens[1])
        else -> {
            val maternal = tokens.last()
            val paternal = tokens[tokens.size - 2]
            val first = tokens.dropLast(2).joinToString(" ")
            Triple(first, paternal, maternal)
        }
    }
}

@Composable
private fun profileFilterChipColors() =
    FilterChipDefaults.filterChipColors(
        containerColor = Color(0xFFE2E8F0),
        labelColor = Color(0xFF334155),
        selectedContainerColor = CyanPrimary,
        selectedLabelColor = Color.White)

@Composable
private fun ProfileEditDialogShell(
    title: String,
    saving: Boolean,
    errorMessage: String?,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = { if (!saving) onCancel() }) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier =
                        Modifier.fillMaxWidth().heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                    content()
                }
                if (errorMessage != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(errorMessage, color = Color(0xFFB00020), fontSize = 13.sp)
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onCancel, enabled = !saving) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onSave,
                        enabled = !saving,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = OrangeAccent, contentColor = Color.White)) {
                        Text(if (saving) "Guardando…" else "Guardar")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileEditModalsHost(
    section: ProfileEditSection?,
    state: ProfileUiState,
    onRegionChange: (Long) -> Unit,
    onCancel: () -> Unit,
    onSave: (CandidateProfilePatchJson) -> Unit
) {
    val raw = state.rawProfile ?: return
    val chipColors = profileFilterChipColors()

    when (section) {
        ProfileEditSection.PERSONAL ->
            PersonalEditModal(
                raw = raw,
                regions = state.regions,
                communes = state.communes,
                sectors = state.sectors,
                saving = state.editSaving,
                errorMessage = state.editModalError,
                onRegionSelected = onRegionChange,
                onCancel = onCancel,
                onSave = onSave)
        ProfileEditSection.DESCRIPTION ->
            DescriptionEditModal(
                initial = raw.profileSummary.orEmpty(),
                saving = state.editSaving,
                errorMessage = state.editModalError,
                onCancel = onCancel,
                onSave = { text -> onSave(CandidateProfilePatchJson(profileSummary = text)) })
        ProfileEditSection.SALARY -> {
            val min = raw.salaryExpectedMin?.toFloat() ?: SALARY_SLIDER_DEFAULT.start
            val max = raw.salaryExpectedMax?.toFloat() ?: SALARY_SLIDER_DEFAULT.endInclusive
            SalaryEditModal(
                initialRange = snapSalary(min)..snapSalary(max),
                saving = state.editSaving,
                errorMessage = state.editModalError,
                onCancel = onCancel,
                onSave = { range ->
                    onSave(
                        CandidateProfilePatchJson(
                            salaryExpectedMin = range.start.roundToInt(),
                            salaryExpectedMax = range.endInclusive.roundToInt()))
                })
        }
        ProfileEditSection.YEARS ->
            YearsEditModal(
                initial = raw.yearsExperience,
                saving = state.editSaving,
                errorMessage = state.editModalError,
                onCancel = onCancel,
                onSave = { years ->
                    onSave(CandidateProfilePatchJson(yearsExperience = years))
                })
        ProfileEditSection.MODALITIES ->
            ChipSelectionModal(
                title = "Modalidades preferidas",
                options = MODALITIES,
                initial = raw.preferredModalities.toSet(),
                saving = state.editSaving,
                errorMessage = state.editModalError,
                chipColors = chipColors,
                onCancel = onCancel,
                onSave = { selected ->
                    onSave(CandidateProfilePatchJson(preferredModalities = selected.toList()))
                })
        ProfileEditSection.WORKLOADS ->
            ChipSelectionModal(
                title = "Cargas horarias preferidas",
                options = WORKLOADS,
                initial = raw.preferredWorkloads.toSet(),
                saving = state.editSaving,
                errorMessage = state.editModalError,
                chipColors = chipColors,
                onCancel = onCancel,
                onSave = { selected ->
                    onSave(CandidateProfilePatchJson(preferredWorkloads = selected.toList()))
                })
        ProfileEditSection.SKILLS ->
            SkillsEditModal(
                skills = state.skillsCatalog,
                initial = raw.skills.map { it.id }.toSet(),
                saving = state.editSaving,
                errorMessage = state.editModalError,
                chipColors = chipColors,
                onCancel = onCancel,
                onSave = { ids -> onSave(CandidateProfilePatchJson(skillsIds = ids.toList())) })
        null -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonalEditModal(
    raw: com.worksi.app.data.model.CandidateProfileJson,
    regions: List<CatalogItemDto>,
    communes: List<CatalogItemDto>,
    sectors: List<CatalogItemDto>,
    saving: Boolean,
    errorMessage: String?,
    onRegionSelected: (Long) -> Unit,
    onCancel: () -> Unit,
    onSave: (CandidateProfilePatchJson) -> Unit
) {
    val middle = raw.middleName?.trim().orEmpty()
    val maternal = raw.lastNameMaternal?.trim().orEmpty()
    val initialName =
        buildString {
            append(raw.firstName.trim())
            if (middle.isNotEmpty()) {
                append(' ')
                append(middle)
            }
            append(' ')
            append(raw.lastNamePaternal.trim())
            if (maternal.isNotEmpty()) {
                append(' ')
                append(maternal)
            }
        }.trim()
    var nombre by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(raw.email.trim()) }
    var phone by remember { mutableStateOf(raw.phone?.trim().orEmpty()) }
    var regionId by remember { mutableStateOf(raw.regionId) }
    var communeId by remember { mutableStateOf(raw.communeId) }
    var sectorId by remember { mutableStateOf(raw.sectorId) }
    var regionMenu by remember { mutableStateOf(false) }
    var communeMenu by remember { mutableStateOf(false) }
    var sectorMenu by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    ProfileEditDialogShell(
        title = "Información personal",
        saving = saving,
        errorMessage = errorMessage ?: localError,
        onCancel = onCancel,
        onSave = {
            localError = null
            when {
                nombre.isBlank() -> localError = "Completa el nombre"
                email.isBlank() || !EMAIL_REGEX.matches(email.trim()) ->
                    localError = "Correo no válido"
                phone.isBlank() -> localError = "Completa el teléfono"
                sectorId == null -> localError = "Seleccione rubro"
                communeId <= 0L -> localError = "Seleccione comuna"
                else -> {
                    val (first, paternal, maternalName) = parseDisplayName(nombre)
                    if (first.isBlank() || paternal.isBlank() || maternalName.isBlank()) {
                        localError = "Nombre incompleto"
                    } else {
                        onSave(
                            CandidateProfilePatchJson(
                                firstName = first,
                                lastNamePaternal = paternal,
                                lastNameMaternal = maternalName,
                                email = email.trim(),
                                phone = phone.trim(),
                                regionId = regionId,
                                communeId = communeId,
                                sectorId = sectorId))
                    }
                }
            }
        }) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        ExposedDropdownMenuBox(
            expanded = sectorMenu,
            onExpandedChange = { sectorMenu = !sectorMenu }) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                readOnly = true,
                value = sectors.find { it.id == sectorId }?.name.orEmpty(),
                onValueChange = {},
                label = { Text("Rubro") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sectorMenu) })
            DropdownMenu(expanded = sectorMenu, onDismissRequest = { sectorMenu = false }) {
                sectors.forEach { s ->
                    DropdownMenuItem(
                        text = { Text(s.name) },
                        onClick = {
                            sectorId = s.id
                            sectorMenu = false
                        })
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        ExposedDropdownMenuBox(
            expanded = regionMenu,
            onExpandedChange = { regionMenu = !regionMenu }) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                readOnly = true,
                value = regions.find { it.id == regionId }?.name.orEmpty(),
                onValueChange = {},
                label = { Text("Región") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionMenu) })
            DropdownMenu(expanded = regionMenu, onDismissRequest = { regionMenu = false }) {
                regions.forEach { r ->
                    DropdownMenuItem(
                        text = { Text(r.name) },
                        onClick = {
                            regionId = r.id
                            communeId = 0L
                            onRegionSelected(r.id)
                            regionMenu = false
                        })
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        ExposedDropdownMenuBox(
            expanded = communeMenu,
            onExpandedChange = { if (regionId > 0L) communeMenu = !communeMenu }) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                readOnly = true,
                enabled = regionId > 0L,
                value = communes.find { it.id == communeId }?.name.orEmpty(),
                onValueChange = {},
                label = { Text("Comuna") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = communeMenu) })
            DropdownMenu(expanded = communeMenu, onDismissRequest = { communeMenu = false }) {
                communes.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c.name) },
                        onClick = {
                            communeId = c.id
                            communeMenu = false
                        })
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun DescriptionEditModal(
    initial: String,
    saving: Boolean,
    errorMessage: String?,
    onCancel: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(initial) }
    ProfileEditDialogShell(
        title = "Descripción",
        saving = saving,
        errorMessage = errorMessage,
        onCancel = onCancel,
        onSave = { onSave(text.trim()) }) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth().height(160.dp),
            minLines = 5)
    }
}

@Composable
private fun SalaryEditModal(
    initialRange: ClosedFloatingPointRange<Float>,
    saving: Boolean,
    errorMessage: String?,
    onCancel: () -> Unit,
    onSave: (ClosedFloatingPointRange<Float>) -> Unit
) {
    var sliderRange by remember { mutableStateOf(initialRange) }
    LaunchedEffect(initialRange) { sliderRange = initialRange }

    ProfileEditDialogShell(
        title = "Sueldo esperado",
        saving = saving,
        errorMessage = errorMessage,
        onCancel = onCancel,
        onSave = { onSave(sliderRange) }) {
        RangeSlider(
            value = sliderRange,
            onValueChange = { new ->
                var low = snapSalary(new.start)
                var high = snapSalary(new.endInclusive)
                if (low > high) {
                    val t = low
                    low = high
                    high = t
                }
                if (high - low < SALARY_STEP) {
                    high = (low + SALARY_STEP).coerceAtMost(SALARY_RANGE_F.endInclusive)
                    if (high - low < SALARY_STEP) {
                        low = (high - SALARY_STEP).coerceAtLeast(SALARY_RANGE_F.start)
                    }
                }
                sliderRange = low..high
            },
            valueRange = SALARY_RANGE_F,
            modifier = Modifier.fillMaxWidth(),
            colors =
                SliderDefaults.colors(
                    thumbColor = CyanPrimary,
                    activeTrackColor = OrangeAccent,
                    inactiveTrackColor = Color(0xFFE2E8F0)))
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Mínima", fontSize = 12.sp, color = MutedText)
                Text(
                    formatClPesos(sliderRange.start.toLong()),
                    fontWeight = FontWeight.SemiBold,
                    color = CyanPrimary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Máxima", fontSize = 12.sp, color = MutedText)
                Text(
                    formatClPesos(sliderRange.endInclusive.toLong()),
                    fontWeight = FontWeight.SemiBold,
                    color = CyanPrimary)
            }
        }
    }
}

@Composable
private fun YearsEditModal(
    initial: Int,
    saving: Boolean,
    errorMessage: String?,
    onCancel: () -> Unit,
    onSave: (Int) -> Unit
) {
    var years by remember { mutableStateOf(initial.coerceIn(0, MAX_YEARS_EXPERIENCE)) }
    LaunchedEffect(initial) { years = initial.coerceIn(0, MAX_YEARS_EXPERIENCE) }

    ProfileEditDialogShell(
        title = "Años de experiencia",
        saving = saving,
        errorMessage = errorMessage,
        onCancel = onCancel,
        onSave = { onSave(years) }) {
        Text(
            if (years <= 0) "0 años" else "$years años",
            fontWeight = FontWeight.SemiBold,
            color = CyanPrimary,
            fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Slider(
            value = years.toFloat(),
            onValueChange = { years = it.roundToInt() },
            valueRange = 0f..MAX_YEARS_EXPERIENCE.toFloat(),
            steps = MAX_YEARS_EXPERIENCE - 1,
            modifier = Modifier.fillMaxWidth(),
            colors =
                SliderDefaults.colors(
                    thumbColor = CyanPrimary,
                    activeTrackColor = OrangeAccent,
                    inactiveTrackColor = Color(0xFFE2E8F0)))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipSelectionModal(
    title: String,
    options: List<Pair<String, String>>,
    initial: Set<String>,
    saving: Boolean,
    errorMessage: String?,
    chipColors: androidx.compose.material3.SelectableChipColors,
    onCancel: () -> Unit,
    onSave: (Set<String>) -> Unit
) {
    var selected by remember { mutableStateOf(initial) }
    var localError by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(initial) { selected = initial }

    ProfileEditDialogShell(
        title = title,
        saving = saving,
        errorMessage = errorMessage ?: localError,
        onCancel = onCancel,
        onSave = {
            if (selected.isEmpty()) {
                localError = "Seleccione al menos una opción"
            } else {
                localError = null
                onSave(selected)
            }
        }) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { (code, label) ->
                FilterChip(
                    selected = selected.contains(code),
                    onClick = {
                        val next = selected.toMutableSet()
                        if (next.contains(code)) next.remove(code) else next.add(code)
                        selected = next
                    },
                    label = { Text(label) },
                    colors = chipColors)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillsEditModal(
    skills: List<CatalogItemDto>,
    initial: Set<Long>,
    saving: Boolean,
    errorMessage: String?,
    chipColors: androidx.compose.material3.SelectableChipColors,
    onCancel: () -> Unit,
    onSave: (Set<Long>) -> Unit
) {
    var selected by remember { mutableStateOf(initial) }
    var localError by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(initial) { selected = initial }

    ProfileEditDialogShell(
        title = "Skills",
        saving = saving,
        errorMessage = errorMessage ?: localError,
        onCancel = onCancel,
        onSave = {
            when {
                selected.size < 3 -> localError = "Seleccione entre 3 y 12 skills"
                selected.size > 12 -> localError = "Máximo 12 skills"
                else -> {
                    localError = null
                    onSave(selected)
                }
            }
        }) {
        if (skills.isEmpty()) {
            Text("No hay skills para el rubro actual.", color = MutedText, fontSize = 14.sp)
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                skills.forEach { sk ->
                    FilterChip(
                        selected = selected.contains(sk.id),
                        onClick = {
                            val next = selected.toMutableSet()
                            if (next.contains(sk.id)) {
                                next.remove(sk.id)
                            } else if (next.size < 12) {
                                next.add(sk.id)
                            }
                            selected = next
                        },
                        label = { Text(sk.name) },
                        colors = chipColors)
                }
            }
        }
    }
}

private val MutedText = Color(0xFF757575)
