package com.worksi.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worksi.app.data.api.RetrofitClient
import com.worksi.app.data.model.LoginNoticeItemJson
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

enum class MainTab {
  Profile,
  Offers,
  Saved,
  Applications
}

object CandidateLoginNoticeHolder {
  private val _notice = MutableStateFlow<LoginNoticeItemJson?>(null)
  val notice: StateFlow<LoginNoticeItemJson?> = _notice.asStateFlow()

  suspend fun refresh() {
    val next =
        withContext(Dispatchers.IO) {
          val response = RetrofitClient.messagingApi.getLoginNotice()
          if (response.isSuccessful) response.body()?.item else null
        }
    _notice.value = next
  }

  suspend fun dismiss(conversationId: Long) {
    withContext(Dispatchers.IO) {
      RetrofitClient.messagingApi.dismissLoginNotice(conversationId)
    }
    _notice.value = null
  }

  fun clear() {
    _notice.value = null
  }
}

object CandidateUnreadChatsHolder {
  const val POLL_INTERVAL_MS = 4_000L

  private val _count = MutableStateFlow(0)
  val count: StateFlow<Int> = _count.asStateFlow()

  private val _listRefreshRequest = MutableStateFlow(0)
  val listRefreshRequest: StateFlow<Int> = _listRefreshRequest.asStateFlow()

  fun requestListRefresh() {
    _listRefreshRequest.value++
  }

  suspend fun refresh() {
    val next =
        withContext(Dispatchers.IO) {
          val response = RetrofitClient.messagingApi.listConversations(page = 1, size = 100)
          if (response.isSuccessful) {
            response.body()?.items?.count { it.unreadCount > 0 } ?: 0
          } else {
            0
          }
        }
    _count.value = next
  }
}

private val NavIconSize = 28.dp
private val NavLabelSize = 12.sp
private val TopActionIconSize = 28.dp
private val TopActionsGap = 14.dp

@Composable
fun CandidateTopSessionActions(
    onNavigateToMatches: () -> Unit,
    onLogout: () -> Unit
) {
  val unreadChats by CandidateUnreadChatsHolder.count.collectAsState()
  var showMenu by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    while (isActive) {
      CandidateUnreadChatsHolder.refresh()
      delay(CandidateUnreadChatsHolder.POLL_INTERVAL_MS)
    }
  }

  Row(
      modifier = Modifier.padding(end = 6.dp),
      verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onNavigateToMatches, modifier = Modifier.size(46.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (unreadChats > 0) {
              Text(
                  text = unreadChats.toString(),
                  color = White,
                  fontSize = 20.sp,
                  lineHeight = 28.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(end = 1.dp))
            }
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Matchs",
                tint = if (unreadChats > 0) OrangeAccent else White,
                modifier = Modifier.size(TopActionIconSize))
          }
        }
        Spacer(modifier = Modifier.width(TopActionsGap))
        Box {
          IconButton(onClick = { showMenu = true }, modifier = Modifier.size(46.dp)) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Configuración",
                tint = White,
                modifier = Modifier.size(TopActionIconSize))
          }
          DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(
                text = { Text("Cerrar sesión") },
                onClick = {
                  showMenu = false
                  onLogout()
                })
          }
        }
      }
}

@Composable
fun CandidateMainBottomBar(
    selected: MainTab? = null,
    onProfile: () -> Unit,
    onOffers: () -> Unit,
    onSaved: () -> Unit,
    onApplications: () -> Unit
) {
  NavigationBar(containerColor = CyanPrimary) {
    NavigationBarItem(
        selected = selected != null && selected == MainTab.Offers,
        onClick = onOffers,
        icon = {
          Icon(
              Icons.Filled.Home,
              contentDescription = "Ofertas",
              tint = White,
              modifier = Modifier.size(NavIconSize))
        },
        label = { Text("Ofertas", color = White, fontSize = NavLabelSize) },
        colors = mainNavColors())
    NavigationBarItem(
        selected = selected != null && selected == MainTab.Applications,
        onClick = onApplications,
        icon = {
          Icon(
              Icons.Filled.Menu,
              contentDescription = "Postulaciones",
              tint = White,
              modifier = Modifier.size(NavIconSize))
        },
        label = { Text("Postulaciones", color = White, fontSize = NavLabelSize) },
        colors = mainNavColors())
    NavigationBarItem(
        selected = selected != null && selected == MainTab.Saved,
        onClick = onSaved,
        icon = {
          Icon(
              Icons.Filled.Bookmark,
              contentDescription = "Guardadas",
              tint = White,
              modifier = Modifier.size(NavIconSize))
        },
        label = { Text("Guardadas", color = White, fontSize = NavLabelSize) },
        colors = mainNavColors())
    NavigationBarItem(
        selected = selected != null && selected == MainTab.Profile,
        onClick = onProfile,
        icon = {
          Icon(
              Icons.Filled.Person,
              contentDescription = "Perfil",
              tint = White,
              modifier = Modifier.size(NavIconSize))
        },
        label = { Text("Perfil", color = White, fontSize = NavLabelSize) },
        colors = mainNavColors())
  }
}

@Composable
private fun mainNavColors() =
    NavigationBarItemDefaults.colors(
        unselectedIconColor = White,
        unselectedTextColor = White,
        selectedIconColor = White,
        selectedTextColor = White,
        indicatorColor = CyanPrimary)
