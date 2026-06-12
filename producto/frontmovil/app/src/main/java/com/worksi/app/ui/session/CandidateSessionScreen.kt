package com.worksi.app.ui.session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.worksi.app.ui.home.HomeScreen
import com.worksi.app.ui.home.HomeViewModel

@Composable
fun CandidateSessionScreen(
    homeViewModel: HomeViewModel,
    onNavigateToProfile: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onNavigateToApplications: () -> Unit,
    onNavigateToMatches: () -> Unit,
    onLogout: () -> Unit,
    onOpenJobDetail: (Long) -> Unit,
    onOpenApplicationPreview: (Long) -> Unit
) {
  Box(modifier = Modifier.fillMaxSize()) {
    HomeScreen(
        viewModel = homeViewModel,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToSaved = onNavigateToSaved,
        onNavigateToApplications = onNavigateToApplications,
        onNavigateToMatches = onNavigateToMatches,
        onSettings = { },
        onLogout = onLogout,
        onOpenJobDetail = onOpenJobDetail,
        onOpenApplicationPreview = onOpenApplicationPreview)
  }
}
