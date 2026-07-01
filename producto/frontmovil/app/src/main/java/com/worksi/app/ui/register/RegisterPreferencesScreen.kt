package com.worksi.app.ui.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White
import com.worksi.app.ui.theme.worksiOnCyanFilterChipColors
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min
import kotlin.math.roundToInt

private val RegisterBackButtonRowHeight = 56.dp

private const val SALARY_STEP = 50_000f
private val SALARY_RANGE_F = 300_000f..8_000_000f
private val SALARY_SLIDER_DEFAULT = 1_000_000f..2_500_000f
private const val MAX_YEARS_EXPERIENCE = 50

private val MatchingNoticeBackground = Color.White
private val MatchingNoticeBorder = Color(0x59F97316)
private val MatchingNoticeText = Color(0xFFC2410C)

private fun snapSalary(value: Float): Float {
    val stepped = (value / SALARY_STEP).roundToInt() * SALARY_STEP
    return stepped.coerceIn(SALARY_RANGE_F.start, SALARY_RANGE_F.endInclusive)
}

private fun formatClPesos(amount: Long): String {
    val fmt = NumberFormat.getNumberInstance(Locale("es", "CL"))
    return "$${fmt.format(amount)}"
}

private val MODALITIES = listOf(
    "REMOTE" to "Remoto",
    "HYBRID" to "Hibrido",
    "ONSITE" to "Presencial"
)
private val WORKLOADS = listOf(
    "FULL_TIME" to "Jornada completa",
    "PART_TIME" to "Part time",
    "OTHER" to "Otro"
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterPreferencesScreen(
    viewModel: CandidateRegisterViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val state by viewModel.ui.collectAsState()
    val d = state.draft
    var showErrors by remember { mutableStateOf(false) }
    val chipColors = worksiOnCyanFilterChipColors()

    var sliderRange by remember { mutableStateOf(SALARY_SLIDER_DEFAULT) }

    LaunchedEffect(d.salaryMin, d.salaryMax) {
        val mn = d.salaryMin.toLongOrNull()
        val mx = d.salaryMax.toLongOrNull()
        if (mn != null && mx != null && mn <= mx) {
            val low = snapSalary(mn.toFloat())
            val high = snapSalary(mx.toFloat())
            val ordered = min(low, high)..maxOf(low, high)
            sliderRange = ordered
        } else {
            sliderRange = SALARY_SLIDER_DEFAULT
        }
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
            Text("Preferencias", style = MaterialTheme.typography.headlineSmall, color = White)
            Spacer(Modifier.height(12.dp))
            RegisterOutlinedTextField(
                value = d.profileSummary,
                onValueChange = { s -> viewModel.updateDraft { it.copy(profileSummary = s) } },
                label = { Text("Presentacion personal (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3
            )
            Spacer(Modifier.height(16.dp))
            Text("Renta esperada (opcional)", color = White.copy(alpha = 0.9f))
            Spacer(Modifier.height(4.dp))
            Text(
                "Ajusta los dos puntos en la linea. Si no la mueves, no se guarda rango de renta.",
                style = MaterialTheme.typography.bodySmall,
                color = White.copy(alpha = 0.75f)
            )
            Spacer(Modifier.height(12.dp))
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
                    val ordered = low..high
                    sliderRange = ordered
                    viewModel.updateDraft {
                        it.copy(
                            salaryMin = ordered.start.toLong().toString(),
                            salaryMax = ordered.endInclusive.toLong().toString()
                        )
                    }
                },
                valueRange = SALARY_RANGE_F,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = White,
                    activeTrackColor = OrangeAccent,
                    inactiveTrackColor = White.copy(alpha = 0.35f),
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                )
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Minima", style = MaterialTheme.typography.labelMedium, color = White.copy(alpha = 0.75f))
                    Text(
                        formatClPesos(sliderRange.start.toLong()),
                        style = MaterialTheme.typography.titleMedium,
                        color = White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Maxima", style = MaterialTheme.typography.labelMedium, color = White.copy(alpha = 0.75f))
                    Text(
                        formatClPesos(sliderRange.endInclusive.toLong()),
                        style = MaterialTheme.typography.titleMedium,
                        color = White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "${formatClPesos(SALARY_RANGE_F.start.toLong())} — ${formatClPesos(SALARY_RANGE_F.endInclusive.toLong())}",
                style = MaterialTheme.typography.bodySmall,
                color = White.copy(alpha = 0.55f)
            )
            Spacer(Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MatchingNoticeBackground,
                border = BorderStroke(1.dp, MatchingNoticeBorder)
            ) {
                Text(
                    "Atencion: tus modalidades, cargas horarias y anos de experiencia declarados se consideran en el porcentaje de matching.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MatchingNoticeText,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("Anos de experiencia laboral (*)", color = White.copy(alpha = 0.9f))
            Spacer(Modifier.height(4.dp))
            Text(
                "Indica cuantos anos de experiencia profesional tienes (0 si eres recien egresado).",
                style = MaterialTheme.typography.bodySmall,
                color = White.copy(alpha = 0.75f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (d.yearsExperience <= 0) "0 anos" else "${d.yearsExperience} anos",
                style = MaterialTheme.typography.titleMedium,
                color = White,
                fontWeight = FontWeight.SemiBold
            )
            Slider(
                value = d.yearsExperience.toFloat(),
                onValueChange = { v ->
                    viewModel.updateDraft { it.copy(yearsExperience = v.roundToInt()) }
                },
                valueRange = 0f..MAX_YEARS_EXPERIENCE.toFloat(),
                steps = MAX_YEARS_EXPERIENCE - 1,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = White,
                    activeTrackColor = OrangeAccent,
                    inactiveTrackColor = White.copy(alpha = 0.35f)
                )
            )
            Spacer(Modifier.height(16.dp))
            Text("Modalidades preferidas (* al menos una)", color = White.copy(alpha = 0.9f))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MODALITIES.forEach { (code, label) ->
                    FilterChip(
                        selected = d.modalities.contains(code),
                        onClick = { viewModel.toggleModality(code) },
                        label = { Text(label) },
                        colors = chipColors
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Cargas horarias preferidas (* al menos una)", color = White.copy(alpha = 0.9f))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WORKLOADS.forEach { (code, label) ->
                    FilterChip(
                        selected = d.workloads.contains(code),
                        onClick = { viewModel.toggleWorkload(code) },
                        label = { Text(label) },
                        colors = chipColors
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            RegisterStepErrorHints(d.preferencesStepValidationMessages(showErrors))
            Button(
                onClick = {
                    showErrors = true
                    if (d.isPreferencesStepValid()) onNext()
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

private fun RegisterDraft.isPreferencesStepValid(): Boolean =
    preferencesStepValidationMessages(true).isEmpty()

private fun RegisterDraft.preferencesStepValidationMessages(showErrors: Boolean): List<String> {
    if (!showErrors) return emptyList()
    val messages = mutableListOf<String>()
    if (modalities.isEmpty() || workloads.isEmpty()) {
        messages.add("Selecciona al menos una modalidad y una carga horaria.")
    }
    val minV = salaryMin.toLongOrNull()
    val maxV = salaryMax.toLongOrNull()
    if (salaryMin.isNotEmpty() && salaryMax.isNotEmpty() && minV != null && maxV != null && minV > maxV) {
        messages.add("El minimo de renta no puede ser mayor que el maximo.")
    }
    return messages
}
