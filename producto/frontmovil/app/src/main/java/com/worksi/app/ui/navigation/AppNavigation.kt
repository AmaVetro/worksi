package com.worksi.app.ui.navigation

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.worksi.app.ui.applications.ApplicationPreviewScreen
import com.worksi.app.ui.applications.ApplicationsScreen
import com.worksi.app.ui.applications.ApplicationsViewModel
import com.worksi.app.ui.jobdetail.JobDetailScreen
import com.worksi.app.data.local.SecureTokenStore
import com.worksi.app.data.saved.CandidateSavedJobsStore
import com.worksi.app.ui.home.HomeScreen
import com.worksi.app.ui.home.HomeViewModel
import com.worksi.app.ui.profile.ProfileScreen
import com.worksi.app.ui.profile.ProfileViewModel
import com.worksi.app.ui.saved.SavedJobsScreen
import com.worksi.app.ui.login.LoginScreen
import com.worksi.app.ui.login.LoginViewModel
import com.worksi.app.ui.recovery.RecoveryCodeScreen
import com.worksi.app.ui.recovery.RecoveryEmailScreen
import com.worksi.app.ui.recovery.RecoveryLockedScreen
import com.worksi.app.ui.recovery.RecoveryNewPasswordScreen
import com.worksi.app.ui.recovery.RecoverySuccessScreen
import com.worksi.app.ui.recovery.RecoveryViewModel
import com.worksi.app.ui.register.CandidateRegisterViewModel
import com.worksi.app.ui.register.RegisterConsentScreen
import com.worksi.app.ui.register.RegisterCvScreen
import com.worksi.app.ui.register.RegisterPersonalScreen
import com.worksi.app.ui.register.RegisterPreferencesScreen
import com.worksi.app.ui.register.RegisterSkillsScreen
import com.worksi.app.ui.splash.SplashScreen
import com.worksi.app.ui.welcome.WelcomeScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Session : Screen("session")
    object Profile : Screen("profile")
    object Saved : Screen("saved")
    object Applications : Screen("applications")
    object RegisterPersonal : Screen("register_personal")
    object RegisterCv : Screen("register_cv")
    object RegisterSkills : Screen("register_skills")
    object RegisterPreferences : Screen("register_preferences")
    object RegisterConsent : Screen("register_consent")
    object RecoveryLocked : Screen("recovery_locked")
    object RecoveryEmail : Screen("recovery_email")
    object RecoveryCode : Screen("recovery_code")
    object RecoveryNewPassword : Screen("recovery_new_password")
    object RecoverySuccess : Screen("recovery_success")
}

private const val JobDetailRoute = "job_detail/{jobId}"
private const val ApplicationPreviewRoute = "application_preview/{applicationId}"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val activity = LocalContext.current as ComponentActivity
    val app = LocalContext.current.applicationContext as Application
    val recoveryVm: RecoveryViewModel = viewModel(viewModelStoreOwner = activity)
    val registerVm: CandidateRegisterViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app)
    )

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onFinished = {
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.RegisterPersonal.route) }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRecovery = {
                    recoveryVm.resetFlow()
                    navController.navigate(Screen.RecoveryEmail.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Session.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onNavigateToLocked = { navController.navigate(Screen.RecoveryLocked.route) }
            )
        }

        composable(Screen.Session.route) {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSaved = { navController.navigate(Screen.Saved.route) },
                onNavigateToApplications = { navController.navigate(Screen.Applications.route) },
                onSettings = { },
                onLogout = {
                    SecureTokenStore.clear()
                    CandidateSavedJobsStore.clear()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenJobDetail = { jobId -> navController.navigate("job_detail/$jobId") },
                onOpenApplicationPreview = { appId ->
                    navController.navigate("application_preview/$appId")
                })
        }

        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel()
            ProfileScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.popBackStack(Screen.Session.route, inclusive = false)
                },
                onNavigateToSaved = { navController.navigate(Screen.Saved.route) },
                onNavigateToApplications = { navController.navigate(Screen.Applications.route) },
                onLogout = {
                    SecureTokenStore.clear()
                    CandidateSavedJobsStore.clear()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                })
        }

        composable(
            route = JobDetailRoute,
            arguments = listOf(navArgument("jobId") { type = NavType.LongType })) { entry ->
              val jobId = entry.arguments?.getLong("jobId") ?: 0L
              if (jobId <= 0L) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                Box(modifier = Modifier.fillMaxSize())
              } else {
                JobDetailScreen(jobId = jobId, onBack = { navController.popBackStack() })
              }
            }

        composable(Screen.Saved.route) {
            SavedJobsScreen(
                onNavigateToHome = {
                    navController.popBackStack(Screen.Session.route, inclusive = false)
                },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToApplications = { navController.navigate(Screen.Applications.route) },
                onOpenJobDetail = { jobId -> navController.navigate("job_detail/$jobId") },
                onLogout = {
                    SecureTokenStore.clear()
                    CandidateSavedJobsStore.clear()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                })
        }

        composable(Screen.Applications.route) { entry ->
            val applicationsViewModel: ApplicationsViewModel = viewModel(entry)
            val refresh by entry.savedStateHandle.getStateFlow("refresh_applications", false)
                .collectAsState()
            LaunchedEffect(refresh) {
                if (refresh) {
                    applicationsViewModel.retry()
                    entry.savedStateHandle["refresh_applications"] = false
                }
            }
            ApplicationsScreen(
                viewModel = applicationsViewModel,
                onNavigateToHome = {
                    navController.popBackStack(Screen.Session.route, inclusive = false)
                },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSaved = { navController.navigate(Screen.Saved.route) },
                onOpenPreview = { appId ->
                    navController.navigate("application_preview/$appId")
                },
                onLogout = {
                    SecureTokenStore.clear()
                    CandidateSavedJobsStore.clear()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                })
        }

        composable(
            route = ApplicationPreviewRoute,
            arguments = listOf(navArgument("applicationId") { type = NavType.LongType })) { entry ->
              val appId = entry.arguments?.getLong("applicationId") ?: 0L
              if (appId <= 0L) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                Box(modifier = Modifier.fillMaxSize())
              } else {
                ApplicationPreviewScreen(
                    applicationId = appId,
                    onBack = { navController.popBackStack() },
                    onGoToJob = { jobId ->
                      navController.navigate("job_detail/$jobId")
                    },
                    onCancelled = {
                      runCatching {
                        navController
                            .getBackStackEntry(Screen.Applications.route)
                            .savedStateHandle["refresh_applications"] = true
                      }
                      navController.popBackStack()
                    })
              }
            }

        composable(Screen.RegisterPersonal.route) {
            RegisterPersonalScreen(
                viewModel = registerVm,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.RegisterCv.route) }
            )
        }

        composable(Screen.RegisterCv.route) {
            RegisterCvScreen(
                viewModel = registerVm,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.RegisterSkills.route) }
            )
        }

        composable(Screen.RegisterSkills.route) {
            RegisterSkillsScreen(
                viewModel = registerVm,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.RegisterPreferences.route) }
            )
        }

        composable(Screen.RegisterPreferences.route) {
            RegisterPreferencesScreen(
                viewModel = registerVm,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.RegisterConsent.route) }
            )
        }

        composable(Screen.RegisterConsent.route) {
            RegisterConsentScreen(
                viewModel = registerVm,
                onBack = { navController.popBackStack() },
                onRegistered = {
                    registerVm.reset()
                    navController.navigate(Screen.Session.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Welcome.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.RecoveryLocked.route) {
            RecoveryLockedScreen(
                onNavigateToRecovery = {
                    recoveryVm.resetFlow()
                    navController.navigate(Screen.RecoveryEmail.route)
                },
                onNavigateToLogin = {
                    navController.popBackStack(Screen.Login.route, inclusive = false)
                }
            )
        }

        composable(Screen.RecoveryEmail.route) {
            RecoveryEmailScreen(
                viewModel = recoveryVm,
                onCodeSent = { navController.navigate(Screen.RecoveryCode.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RecoveryCode.route) {
            RecoveryCodeScreen(
                viewModel = recoveryVm,
                onCodeVerified = { navController.navigate(Screen.RecoveryNewPassword.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RecoveryNewPassword.route) {
            RecoveryNewPasswordScreen(
                viewModel = recoveryVm,
                onPasswordReset = { navController.navigate(Screen.RecoverySuccess.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RecoverySuccess.route) {
            RecoverySuccessScreen(
                onBackToLogin = {
                    recoveryVm.resetFlow()
                    navController.popBackStack(Screen.Login.route, inclusive = false)
                }
            )
        }
    }
}