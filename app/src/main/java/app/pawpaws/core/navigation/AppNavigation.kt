package app.pawpaws.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import app.pawpaws.features.forgot.PasswordRecoveryScreen
import app.pawpaws.features.forgot.PasswordResetScreen
import app.pawpaws.features.home.HomeScreen
import app.pawpaws.features.login.LoginScreen
import app.pawpaws.features.register.RegisterScreen

object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot-password"
    const val RESET = "reset_password"
}

@Composable
fun AppNavigation(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN)
                }

            )
        }
        composable(Routes.FORGOT) {
            PasswordRecoveryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToReset = {
                    navController.navigate(Routes.RESET)
                }
            )
        }

        composable(Routes.RESET) {
            PasswordResetScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPasswordResetSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
    }
}