package com.worksi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import com.worksi.app.data.model.MatchBreakdownJson
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worksi.app.ui.theme.CyanPrimary

private val MatchHigh = Color(0xFF4ADE80)
private val MatchMid = Color(0xFFFDE047)
private val MatchLow = Color(0xFFF87171)
private val MatchTrack = Color(0xFFE2E8F0)
private val MatchNone = Color(0xFFCBD5E1)

fun matchScorePercent(score: Double?): Int? {
  if (score == null || score.isNaN()) return null
  return score.toInt().coerceIn(0, 100)
}

fun matchBarColor(percent: Int?): Color =
    when {
      percent == null -> MatchNone
      percent >= 75 -> MatchHigh
      percent >= 25 -> MatchMid
      else -> MatchLow
    }

fun modalityLabel(raw: String): String =
    when (raw.uppercase()) {
      "REMOTE" -> "Remoto"
      "HYBRID" -> "Híbrido"
      "ONSITE" -> "Presencial"
      else -> raw
    }

fun workloadLabel(raw: String): String =
    when (raw.uppercase()) {
      "FULL_TIME" -> "Full time"
      "PART_TIME" -> "Part time"
      "OTHER" -> "Otro"
      else -> raw
    }

@Composable
fun MatchScoreRow(
    score: Double?,
    modifier: Modifier = Modifier,
    barWidth: Dp = 120.dp,
    labelColor: Color = CyanPrimary
) {
  val percent = matchScorePercent(score)
  val label = if (percent != null) "Match: $percent%" else "Match: —"
  val fillFraction = (percent ?: 0) / 100f

  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    Text(
        text = label,
        color = labelColor,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.width(8.dp))
    Box(
        modifier =
            Modifier.width(barWidth)
                .height(16.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(MatchTrack)) {
          Box(
              modifier =
                  Modifier.fillMaxHeight()
                      .fillMaxWidth(fillFraction.coerceIn(0f, 1f))
                      .clip(RoundedCornerShape(999.dp))
                      .background(matchBarColor(percent)))
        }
  }
}

private val BreakdownBarColor = Color(0xFF0F766E)
private val BreakdownBarTrack = Color(0xFFE2E8F0)
private val MutedGray = Color(0xFF64748B)

private data class MatchDimension(val label: String, val score: Double?)

private fun matchDimensions(breakdown: MatchBreakdownJson): List<MatchDimension> =
    listOf(
        MatchDimension("Descripción de la oferta", breakdown.descriptionScore),
        MatchDimension("Título de la oferta", breakdown.titleScore),
        MatchDimension("Modalidad", breakdown.modalityScore),
        MatchDimension("Carga horaria", breakdown.workloadScore),
        MatchDimension("Años de experiencia", breakdown.experienceScore))

@Composable
fun MatchDimensionBarRow(label: String, percent: Int, modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth()) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      Text(label, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.weight(1f))
      Text("$percent%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    }
    Spacer(modifier = Modifier.height(4.dp))
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(BreakdownBarTrack)) {
          Box(
              modifier =
                  Modifier.fillMaxHeight()
                      .fillMaxWidth((percent / 100f).coerceIn(0f, 1f))
                      .clip(RoundedCornerShape(999.dp))
                      .background(BreakdownBarColor))
        }
  }
}

@Composable
fun MatchBreakdownContent(
    breakdown: MatchBreakdownJson?,
    fallbackScore: Double?,
    modifier: Modifier = Modifier
) {
  val finalScore =
      breakdown?.finalScore?.let { matchScorePercent(it) } ?: matchScorePercent(fallbackScore)

  Column(modifier = modifier.fillMaxWidth()) {
    if (finalScore != null) {
      Row(modifier = Modifier.padding(bottom = 16.dp)) {
        Text("Score final: ", fontSize = 18.sp, color = Color.Black)
        Text("$finalScore%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
      }
    } else {
      Text(
          "No hay score disponible para esta oferta.",
          color = Color(0xFFB00020),
          fontSize = 14.sp,
          modifier = Modifier.padding(bottom = 12.dp))
    }
    if (breakdown != null) {
      matchDimensions(breakdown).forEach { dim ->
        val pct = dim.score?.toInt()?.coerceIn(0, 100) ?: 0
        MatchDimensionBarRow(label = dim.label, percent = pct, modifier = Modifier.padding(bottom = 16.dp))
      }
    } else if (finalScore != null) {
      Text(
          "Desglose por dimensión no disponible para esta oferta.",
          color = MutedGray,
          fontSize = 14.sp)
    }
  }
}
