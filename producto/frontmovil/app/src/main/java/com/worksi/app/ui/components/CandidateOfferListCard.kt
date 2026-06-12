package com.worksi.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worksi.app.ui.theme.CyanPrimary

private val MutedText = Color(0xFF757575)

@Composable
fun CandidateOfferListCard(
    title: String,
    company: String,
    salary: Int,
    matchPercentage: Float?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null
) {
  Card(
      modifier =
          modifier
              .fillMaxWidth()
              .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
              text = title,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = CyanPrimary,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis)
          Spacer(Modifier.height(6.dp))
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = company,
                    color = MutedText,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f))
                Text(text = "$$salary", color = MutedText, fontSize = 14.sp)
              }
          Spacer(Modifier.height(8.dp))
          MatchProgressBarRow(matchPercentage = matchPercentage)
          if (bottomContent != null) {
            Spacer(Modifier.height(12.dp))
            bottomContent()
          }
        }
      }
}
