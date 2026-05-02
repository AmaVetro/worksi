package com.worksi.app.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.worksi.app.ui.login.LoginScreen
import com.worksi.app.ui.recovery.RecoveryCodeScreen
import com.worksi.app.ui.recovery.RecoveryEmailScreen
import com.worksi.app.ui.recovery.RecoveryNewPasswordScreen
import com.worksi.app.ui.recovery.RecoverySuccessScreen
import com.worksi.app.ui.recovery.RecoveryViewModel
import com.worksi.app.ui.register.RegisterScreen
import com.worksi.app.ui.splash.SplashScreen
import com.worksi.app.ui.login.LoginViewModel
import com.worksi.app.ui.recovery.RecoveryLockedScreen
import com.worksi.app.data.local.SecureTokenStore
import com.worksi.app.ui.session.SessionPlaceholderScreen
import com.worksi.app.ui.welcome.WelcomeScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Session : Screen("session")
    object Register : Screen("register")
    object RecoveryLocked : Screen("recovery_locked")
    object RecoveryEmail : Screen("recovery_email")
    object RecoveryCode : Screen("recovery_code")
    object RecoveryNewPassword : Screen("recovery_new_password")
    object RecoverySuccess : Screen("recovery_success")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val activity = LocalContext.current as ComponentActivity
    val recoveryVm: RecoveryViewModel = viewModel(viewModelStoreOwner = activity)

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
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
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
            SessionPlaceholderScreen(
                onLogout = {
                    SecureTokenStore.clear()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.RecoveryLocked.route) {
            RecoveryLockedScreen(
                onNavigateToRecovery = {
                    recoveryVm.resetFlow()
                    navController.navigate(Screen.RecoveryEmail.route)
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
