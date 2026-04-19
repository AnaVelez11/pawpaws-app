package app.pawpaws.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.pawpaws.domain.model.enums.Rol
import app.pawpaws.features.forgot.PasswordRecoveryScreen
import app.pawpaws.features.forgot.PasswordResetScreen
import app.pawpaws.features.home.HomeScreen
import app.pawpaws.features.login.LoginScreen
import app.pawpaws.features.register.RegisterScreen
import kotlinx.serialization.Serializable

// ── Rutas type-safe ───────────────────────────────────────────────────────────

@Serializable object Home
@Serializable object Login
@Serializable object Register
@Serializable object ForgotPassword
@Serializable object ResetPassword

@Serializable object Main

@Serializable object MainAdmin

// ── Rutas admin type-safe (para usar progresivamente en AdminMainScreen) ──────

@Serializable object AdminPanel
@Serializable object AdminFeed
@Serializable object AdminReportes
@Serializable object AdminUsuarios
@Serializable object AdminHistorial
@Serializable object AdminAprobada
@Serializable data class AdminRevision(val id: String)
@Serializable data class AdminRevisarCaso(val id: String)
@Serializable data class AdminPerfilUsuario(val id: String)

// ── Compatibilidad String routes para NavGraphs internos ─────────────────────
// MainScreen y AdminMainScreen aún usan String routes internamente
// Debo migrar en próxima entrega

object Routes {
    const val SPLASH     = "splash"
    const val HOME       = "home"
    const val LOGIN      = "login"
    const val REGISTER   = "register"
    const val FORGOT     = "forgot_password"
    const val RESET      = "reset_password"
    const val MAIN       = "main"
    const val MAIN_ADMIN = "main_admin"
}

object AdminRoutes {
    const val PANEL          = "admin_panel"
    const val FEED           = "admin_feed"
    const val REPORTES       = "admin_reportes"
    const val USUARIOS       = "admin_usuarios"
    const val HISTORIAL      = "admin_historial"
    const val APROBADA       = "admin_aprobada"
    const val REVISION       = "admin_revision/{id}"
    const val REVISAR_CASO   = "admin_revisar_caso/{id}"
    const val PERFIL_USUARIO = "admin_perfil_usuario/{id}"

    fun revisionRoute(id: String)      = "admin_revision/$id"
    fun revisarCasoRoute(id: String)   = "admin_revisar_caso/$id"
    fun perfilUsuarioRoute(id: String) = "admin_perfil_usuario/$id"
}

// ── AppNavigation ─────────────────────────────────────────────────────────────

@Composable
fun AppNavigation(
    navController: NavHostController,
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    val sessionState by sessionViewModel.sessionState.collectAsState()

    NavHost(
        navController    = navController,
        startDestination = Home
    ) {

        composable<Home> {
            HomeScreen(
                onNavigateToLogin = { navController.navigate(Login) }
            )
        }

        composable<Login> {
            LoginScreen(
                onNavigateToRegister       = { navController.navigate(Register) },
                onNavigateToForgotPassword = { navController.navigate(ForgotPassword) },
                onLoginSuccess             = { rol ->
                    // Bifurcación por rol: USER → Main, ADMIN → MainAdmin
                    val destino = if (rol == Rol.ADMIN) MainAdmin else Main
                    navController.navigate(destino) {
                        popUpTo(Home) { inclusive = true }
                    }
                },
                viewModel = hiltViewModel()
            )
        }

        composable<Register> {
            RegisterScreen(
                onNavigateBack    = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Login) {
                        popUpTo(Register) { inclusive = true }
                    }
                },
                viewModel = hiltViewModel()
            )
        }

        composable<ForgotPassword> {
            PasswordRecoveryScreen(
                onNavigateBack    = { navController.popBackStack() },
                onNavigateToReset = { navController.navigate(ResetPassword) },
                viewModel         = hiltViewModel()
            )
        }

        composable<ResetPassword> {
            PasswordResetScreen(
                onNavigateBack         = { navController.popBackStack() },
                onPasswordResetSuccess = {
                    navController.navigate(Login) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                viewModel = hiltViewModel()
            )
        }

        // ── ROL USER ──────────────────────────────────────────────────────
        composable<Main> {
            MainScreen(
                sessionViewModel  = sessionViewModel,
                rootNavController = navController
            )
        }

        // ── ROL ADMIN / MODERADOR ─────────────────────────────────────────
        composable<MainAdmin> {
            AdminMainScreen(
                rootNavController = navController,
                sessionViewModel  = sessionViewModel
            )
        }
    }
}