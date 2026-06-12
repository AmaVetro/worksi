package com.worksi.app.ui.matches

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.worksi.app.data.model.LoginNoticeItemJson
import com.worksi.app.ui.components.CandidateLoginNoticeHolder
import com.worksi.app.ui.components.CandidateUnreadChatsHolder
import com.worksi.app.ui.theme.CyanPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CandidateLoginNoticeOverlay(
    enabled: Boolean,
    onViewMessages: (Long) -> Unit
) {
  val notice by CandidateLoginNoticeHolder.notice.collectAsState()
  val scope = rememberCoroutineScope()

  LaunchedEffect(enabled) {
    if (!enabled) {
      CandidateLoginNoticeHolder.clear()
      return@LaunchedEffect
    }
    CandidateLoginNoticeHolder.refresh()
    while (isActive) {
      delay(CandidateUnreadChatsHolder.POLL_INTERVAL_MS)
      CandidateLoginNoticeHolder.refresh()
    }
  }

  notice?.let { item ->
    MatchLoginNoticeScreen(
        item = item,
        onViewMessages = {
          val id = item.conversationId
          scope.launch {
            CandidateLoginNoticeHolder.dismiss(id)
            CandidateUnreadChatsHolder.refresh()
            CandidateUnreadChatsHolder.requestListRefresh()
            onViewMessages(id)
          }
        },
        onDismiss = {
          scope.launch {
            CandidateLoginNoticeHolder.dismiss(item.conversationId)
            CandidateUnreadChatsHolder.refresh()
            CandidateUnreadChatsHolder.requestListRefresh()
          }
        })
  }
}

@Composable
fun MatchLoginNoticeScreen(
    item: LoginNoticeItemJson,
    onViewMessages: () -> Unit,
    onDismiss: () -> Unit
) {
  val salaryLabel =
      NumberFormat.getNumberInstance(Locale("es", "CL")).format(item.salaryOffered)

  Dialog(
      onDismissRequest = onDismiss,
      properties =
          DialogProperties(
              dismissOnBackPress = true,
              dismissOnClickOutside = false,
              usePlatformDefaultWidth = false)) {
        Card(
            modifier =
                Modifier.fillMaxWidth(0.92f)
                    .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)) {
              Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                      Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = Color(0xFF64748B))
                    }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                          text = "¡Felicidades! Una empresa hizo match con tu perfil.",
                          color = CyanPrimary,
                          fontSize = 20.sp,
                          fontWeight = FontWeight.Bold,
                          textAlign = TextAlign.Center,
                          lineHeight = 26.sp)
                      Spacer(modifier = Modifier.height(16.dp))
                      Icon(
                          imageVector = Icons.Filled.Celebration,
                          contentDescription = null,
                          tint = OrangeAccent,
                          modifier = Modifier.size(56.dp))
                      Spacer(modifier = Modifier.height(20.dp))
                      Text(
                          text = item.companyCommercialName,
                          color = Color(0xFF0F172A),
                          fontSize = 18.sp,
                          fontWeight = FontWeight.SemiBold,
                          textAlign = TextAlign.Center)
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                          text = item.jobTitle,
                          color = Color(0xFF334155),
                          fontSize = 16.sp,
                          textAlign = TextAlign.Center)
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                          text = "Sueldo: $$salaryLabel",
                          color = Color(0xFF64748B),
                          fontSize = 15.sp,
                          textAlign = TextAlign.Center)
                      if (item.firstMessagePreview.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier.fillMaxWidth()) {
                              Text(
                                  text = item.firstMessagePreview,
                                  color = Color(0xFF475569),
                                  fontSize = 15.sp,
                                  textAlign = TextAlign.Center,
                                  modifier = Modifier.padding(14.dp))
                            }
                      }
                      Spacer(modifier = Modifier.height(24.dp))
                      Button(
                          onClick = onViewMessages,
                          modifier = Modifier.fillMaxWidth(),
                          colors =
                              ButtonDefaults.buttonColors(
                                  containerColor = CyanPrimary, contentColor = White)) {
                            Text("Ver mensajes", fontWeight = FontWeight.Bold)
                          }
                    }
              }
            }
      }
}
