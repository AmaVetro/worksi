package com.worksi.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.White

enum class MainTab {
  Profile,
  Offers,
  Saved,
  Applications
}

private val NavIconSize = 28.dp
private val NavLabelSize = 12.sp

@Composable
fun CandidateSessionSettingsAction(onLogout: () -> Unit) {
  var showMenu by remember { mutableStateOf(false) }
  Box {
    IconButton(onClick = { showMenu = true }) {
      Icon(Icons.Filled.Settings, contentDescription = "Configuración", tint = White)
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

@Composable
fun CandidateMainBottomBar(
    selected: MainTab,
    onProfile: () -> Unit,
    onOffers: () -> Unit,
    onSaved: () -> Unit,
    onApplications: () -> Unit
) {
  NavigationBar(containerColor = CyanPrimary) {
    NavigationBarItem(
        selected = selected == MainTab.Offers,
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
        selected = selected == MainTab.Applications,
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
        selected = selected == MainTab.Saved,
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
        selected = selected == MainTab.Profile,
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
