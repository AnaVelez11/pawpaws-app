package app.pawpaws.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.features.admin.feed.AdminFeedScreen
import app.pawpaws.features.admin.historial.HistorialScreen
import app.pawpaws.features.admin.panel.PanelScreen
import app.pawpaws.features.admin.reporte.ReportesScreen
import app.pawpaws.features.admin.reporte.RevisarCasoScreen
import app.pawpaws.features.admin.revision.AprobadaScreen
import app.pawpaws.features.admin.revision.RevisionScreen
import app.pawpaws.features.admin.usuario.PerfilUsuarioAdminScreen
import app.pawpaws.features.admin.usuario.UsuariosScreen

private data class AdminNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun AdminMainScreen(
    rootNavController: androidx.navigation.NavHostController,
    sessionViewModel: SessionViewModel
) {
    val adminNavController = rememberNavController()

    val items = listOf(
        AdminNavItem("Panel",    Icons.Default.BarChart, AdminRoutes.PANEL),
        AdminNavItem("Feed",     Icons.Default.Home,     AdminRoutes.FEED),
        AdminNavItem("Reportes", Icons.Default.Flag,     AdminRoutes.REPORTES),
        AdminNavItem("Usuarios", Icons.Default.People,   AdminRoutes.USUARIOS)
    )

    // Ocultar bottom bar en rutas de detalle
    val sinBottomBar = listOf(
        "admin_revision/",
        "admin_revisar_caso/",
        "admin_perfil_usuario/",
        AdminRoutes.HISTORIAL,
        AdminRoutes.APROBADA
    )

    val navBackStackEntry by adminNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val mostrarBottomBar = sinBottomBar.none { currentRoute?.startsWith(it) == true }

    Scaffold(
        bottomBar = {
            if (mostrarBottomBar) {
                NavigationBar(containerColor = Color.White) {
                    items.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick  = {
                                adminNavController.navigate(item.route) {
                                    popUpTo(AdminRoutes.PANEL) { saveState = true }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    tint = if (selected) PawBlue else PawDarkText.copy(alpha = 0.5f)
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    fontSize = 10.sp,
                                    color    = if (selected) PawBlue else PawDarkText.copy(alpha = 0.5f)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PawBlue.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController    = adminNavController,
            startDestination = AdminRoutes.PANEL,
            modifier         = Modifier.padding(innerPadding)
        ) {

            // ── Panel ──────────────────────────────────────────────────────
            composable(AdminRoutes.PANEL) {
                PanelScreen(
                    onHistorial = { adminNavController.navigate(AdminRoutes.HISTORIAL) },
                    viewModel   = hiltViewModel()
                )
            }

            // ── Feed de pendientes ─────────────────────────────────────────
            composable(AdminRoutes.FEED) {
                AdminFeedScreen(
                    onRevisar = { id -> adminNavController.navigate(AdminRoutes.revisionRoute(id)) },
                    viewModel = hiltViewModel()
                )
            }

            // ── Revisión de publicación ────────────────────────────────────
            composable(
                route     = AdminRoutes.REVISION,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStack ->
                val id = backStack.arguments?.getString("id") ?: return@composable
                RevisionScreen(
                    publicacionId  = id,
                    onNavigateBack = { adminNavController.popBackStack() },
                    onAprobada     = {
                        adminNavController.navigate(AdminRoutes.APROBADA) {
                            popUpTo(AdminRoutes.FEED)
                        }
                    },
                    viewModel = hiltViewModel()
                )
            }

            // ── Publicación aprobada ───────────────────────────────────────
            composable(AdminRoutes.APROBADA) {
                AprobadaScreen(
                    onVolverAPendientes = {
                        adminNavController.navigate(AdminRoutes.FEED) {
                            popUpTo(AdminRoutes.PANEL)
                        }
                    }
                )
            }

            // ── Reportes ───────────────────────────────────────────────────
            composable(AdminRoutes.REPORTES) {
                ReportesScreen(
                    onRevisarCaso = { id -> adminNavController.navigate(AdminRoutes.revisarCasoRoute(id)) },
                    viewModel     = hiltViewModel()
                )
            }

            // ── Revisar caso ───────────────────────────────────────────────
            composable(
                route     = AdminRoutes.REVISAR_CASO,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStack ->
                val id = backStack.arguments?.getString("id") ?: return@composable
                RevisarCasoScreen(
                    reporteId      = id,
                    onNavigateBack = { adminNavController.popBackStack() },
                    viewModel      = hiltViewModel()
                )
            }

            // ── Usuarios ───────────────────────────────────────────────────
            composable(AdminRoutes.USUARIOS) {
                UsuariosScreen(
                    onVerPerfil = { id -> adminNavController.navigate(AdminRoutes.perfilUsuarioRoute(id)) },
                    viewModel   = hiltViewModel()
                )
            }

            // ── Perfil usuario (admin) ─────────────────────────────────────
            composable(
                route     = AdminRoutes.PERFIL_USUARIO,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStack ->
                val id = backStack.arguments?.getString("id") ?: return@composable
                PerfilUsuarioAdminScreen(
                    usuarioId      = id,
                    onNavigateBack = { adminNavController.popBackStack() },
                    viewModel      = hiltViewModel()
                )
            }

            // ── Historial ──────────────────────────────────────────────────
            composable(AdminRoutes.HISTORIAL) {
                HistorialScreen(
                    onNavigateBack = { adminNavController.popBackStack() },
                    viewModel      = hiltViewModel()
                )
            }
        }
    }
}