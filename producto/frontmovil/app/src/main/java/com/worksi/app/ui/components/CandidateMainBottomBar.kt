package com.worksi.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.White

enum class MainTab {
  Profile,
  Offers,
  Applications
}

private val NavIconSize = 28.dp
private val NavLabelSize = 14.sp

@Composable
fun CandidateMainBottomBar(
    selected: MainTab,
    onProfile: () -> Unit,
    onOffers: () -> Unit,
    onApplications: () -> Unit
) {
  NavigationBar(containerColor = CyanPrimary) {
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
