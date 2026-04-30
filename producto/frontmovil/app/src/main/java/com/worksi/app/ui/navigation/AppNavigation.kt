package com.worksi.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.worksi.app.ui.login.LoginScreen
import com.worksi.app.ui.recovery.RecoveryCodeScreen
import com.worksi.app.ui.recovery.RecoveryEmailScreen
import com.worksi.app.ui.recovery.RecoveryNewPasswordScreen
import com.worksi.app.ui.recovery.RecoverySuccessScreen
import com.worksi.app.ui.register.RegisterScreen
import com.worksi.app.ui.splash.SplashScreen
import com.worksi.app.ui.login.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.worksi.app.ui.recovery.RecoveryLockedScreen
import com.worksi.app.ui.welcome.WelcomeScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
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
                onNavigateToRecovery = { navController.navigate(Screen.RecoveryEmail.route) },
                onLoginSuccess = { /* Ir a Perfil */ },
                onBack = { navController.popBackStack() },
                onNavigateToLocked = { navController.navigate(Screen.RecoveryLocked.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.RecoveryLocked.route) {
            RecoveryLockedScreen(
                onNavigateToRecovery = { navController.navigate(Screen.RecoveryEmail.route) }
            )
        }

        composable(Screen.RecoveryEmail.route) {
            RecoveryEmailScreen(
                onCodeSent = { navController.navigate(Screen.RecoveryCode.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RecoveryCode.route) {
            RecoveryCodeScreen(
                onCodeVerified = { navController.navigate(Screen.RecoveryNewPassword.route) },
                onNewCode = { /* reenviar código mock */ },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RecoveryNewPassword.route) {
            RecoveryNewPasswordScreen(
                onPasswordReset = { navController.navigate(Screen.RecoverySuccess.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RecoverySuccess.route) {
            RecoverySuccessScreen(
                onBackToLogin = {
                    navController.popBackStack(Screen.Login.route, inclusive = false)
                }
            )
        }
    }
}