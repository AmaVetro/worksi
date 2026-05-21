package com.worksi.app.ui.navigation

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.worksi.app.ui.jobdetail.JobDetailScreen
import com.worksi.app.data.local.SecureTokenStore
import com.worksi.app.ui.home.HomeScreen
import com.worksi.app.ui.home.HomeViewModel
import com.worksi.app.ui.profile.ProfileScreen
import com.worksi.app.ui.profile.ProfileViewModel
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
                onNavigateToMenu = { /* implementar si es necesario */ },
                onLogout = {
                    SecureTokenStore.clear()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenJobDetail = { jobId -> navController.navigate("job_detail/$jobId") }
            )
        }

        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel()
            ProfileScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.popBackStack(Screen.Session.route, inclusive = false)
                },
                onNavigateToMenu = { },
                onLogout = {
                    SecureTokenStore.clear()
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