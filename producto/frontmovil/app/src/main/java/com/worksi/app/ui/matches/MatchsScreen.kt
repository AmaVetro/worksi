package com.worksi.app.ui.matches

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.worksi.app.ui.components.CandidateMainBottomBar
import com.worksi.app.ui.components.CandidateTopSessionActions
import com.worksi.app.ui.components.CandidateUnreadChatsHolder
import com.worksi.app.ui.components.MatchScoreRow
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchsScreen(
    viewModel: MatchsViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onNavigateToApplications: () -> Unit,
    onOpenThread: (Long) -> Unit,
    onLogout: () -> Unit
) {
  val items by viewModel.items.collectAsState()
  val loading by viewModel.loading.collectAsState()
  val error by viewModel.errorMessage.collectAsState()
  val listRefreshRequest by CandidateUnreadChatsHolder.listRefreshRequest.collectAsState()

  LaunchedEffect(listRefreshRequest) {
    if (listRefreshRequest > 0) {
      viewModel.reload()
    }
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Matchs", color = White) },
            actions = {
              CandidateTopSessionActions(onNavigateToMatches = {}, onLogout = onLogout)
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = CyanPrimary))
      },
      bottomBar = {
        CandidateMainBottomBar(
            selected = null,
            onProfile = onNavigateToProfile,
            onOffers = onNavigateToHome,
            onSaved = onNavigateToSaved,
            onApplications = onNavigateToApplications)
      }) { inner ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(inner)
                    .padding(horizontal = 16.dp, vertical = 12.dp)) {
              when {
                loading -> {
                  CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                }
                error != null -> {
                  Text(error ?: "", color = Color(0xFFB00020), fontSize = 15.sp)
                  TextButton(onClick = { viewModel.reload() }) { Text("Reintentar") }
                }
                items.isEmpty() -> {
                  Text(
                      "Aún no tienes conversaciones con empresas.",
                      color = Color(0xFF64748B),
                      fontSize = 15.sp)
                }
                else -> {
                  LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(items, key = { it.conversationId }) { row ->
                      val unread = row.unreadCount
                      val itemTextGap = 7.dp
                      Card(
                          modifier =
                              Modifier.fillMaxWidth()
                                  .clickable { onOpenThread(row.conversationId) },
                          shape = RoundedCornerShape(12.dp),
                          border =
                              if (unread > 0) {
                                BorderStroke(2.dp, OrangeAccent)
                              } else {
                                null
                              },
                          colors =
                              CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))) {
                            Column(modifier = Modifier.padding(14.dp)) {
                              Row(
                                  modifier = Modifier.fillMaxWidth(),
                                  verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        row.companyCommercialName,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanPrimary,
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    MatchScoreRow(
                                        score = row.matchScore,
                                        barWidth = 72.dp,
                                        labelColor = Color(0xFF64748B))
                                    if (unread > 0) {
                                      Spacer(modifier = Modifier.width(8.dp))
                                      Surface(
                                          shape = RoundedCornerShape(6.dp),
                                          color = OrangeAccent) {
                                            Text(
                                                text = unread.toString(),
                                                color = White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier =
                                                    Modifier.padding(
                                                        horizontal = 7.dp, vertical = 4.dp))
                                          }
                                    }
                                  }
                              Spacer(modifier = Modifier.height(itemTextGap))
                              Text(
                                  row.jobTitle,
                                  fontSize = 14.sp,
                                  color = Color(0xFF334155),
                                  maxLines = 1,
                                  overflow = TextOverflow.Ellipsis)
                              if (row.lastMessagePreview.isNotBlank()) {
                                Spacer(modifier = Modifier.height(itemTextGap))
                                Text(
                                    row.lastMessagePreview,
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis)
                              }
                            }
                          }
                    }
                  }
                }
              }
            }
      }
}
