package com.worksi.app.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White

private val BubbleMaxWidth = 300.dp
private val BubbleInnerMaxWidth = 280.dp

private val messageWhenFormatter =
    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale("es", "CL"))

private fun formatMessageWhen(iso: String?): String {
  if (iso.isNullOrBlank()) return ""
  return try {
    messageWhenFormatter.format(
        Instant.parse(iso).atZone(ZoneId.of("America/Santiago")))
  } catch (_: Exception) {
    ""
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchThreadScreen(conversationId: Long, onBack: () -> Unit) {
  val vm: MatchThreadViewModel =
      viewModel(
          key = "match_thread_$conversationId",
          factory =
              object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return MatchThreadViewModel(conversationId) as T
                }
              })
  val header by vm.header.collectAsState()
  val messages by vm.messages.collectAsState()
  val loading by vm.loading.collectAsState()
  val sending by vm.sending.collectAsState()
  val error by vm.errorMessage.collectAsState()
  var draft by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.lastIndex)
    }
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Column {
                Text(
                    header?.companyCommercialName ?: "Empresa",
                    color = White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold)
                header?.jobTitle?.let {
                  Text(it, color = Color(0xFFE0F2FE), fontSize = 13.sp)
                }
              }
            },
            navigationIcon = {
              IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = White)
              }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = CyanPrimary))
      }) { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner)) {
          if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(24.dp))
          }
          if (error != null) {
            Text(
                error ?: "",
                color = Color(0xFFB00020),
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
          }
          LazyColumn(
              state = listState,
              modifier =
                  Modifier.weight(1f)
                      .fillMaxWidth()
                      .background(Color(0xFFF5F5F5))
                      .padding(horizontal = 12.dp, vertical = 8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(messages, key = { it.messageId }) { msg ->
                  val mine = msg.senderRole == "CANDIDATE"
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement =
                          if (mine) Arrangement.End else Arrangement.Start) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = if (mine) CyanPrimary else Color(0xFFE2E8F0),
                            modifier = Modifier.widthIn(max = BubbleMaxWidth)) {
                              Column(
                                  modifier = Modifier.wrapContentWidth().padding(10.dp)) {
                                    Text(
                                        msg.body,
                                        modifier = Modifier.widthIn(max = BubbleInnerMaxWidth),
                                        color = if (mine) White else Color(0xFF1E293B),
                                        fontSize = 15.sp)
                                    val whenLabel = formatMessageWhen(msg.sentAt)
                                    if (whenLabel.isNotBlank()) {
                                      Spacer(modifier = Modifier.height(4.dp))
                                      Text(
                                          text = whenLabel,
                                          modifier = Modifier.align(Alignment.End),
                                          color =
                                              if (mine) {
                                                White.copy(alpha = 0.85f)
                                              } else {
                                                Color(0xFF64748B)
                                              },
                                          fontSize = 11.sp,
                                          textAlign = TextAlign.End)
                                    }
                                  }
                            }
                      }
                }
              }
          Row(
              modifier = Modifier.fillMaxWidth().padding(12.dp),
              verticalAlignment = Alignment.Bottom) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { if (it.length <= 500) draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escriba un mensaje") },
                    maxLines = 4,
                    enabled = !sending && !loading,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            disabledContainerColor = White,
                            focusedTextColor = Color(0xFF1E293B),
                            unfocusedTextColor = Color(0xFF1E293B)))
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                      vm.sendMessage(draft)
                      draft = ""
                    },
                    enabled = !sending && !loading && draft.trim().isNotEmpty(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = OrangeAccent, contentColor = White)) {
                      Text("Enviar", color = White)
                    }
              }
        }
      }
}
