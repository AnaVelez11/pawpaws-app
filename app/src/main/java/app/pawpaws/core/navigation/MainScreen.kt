package app.pawpaws.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.features.feed.FeedScreen
import app.pawpaws.features.mapa.MapaScreen
import app.pawpaws.features.perfil.PerfilScreen
import app.pawpaws.features.publicacion.PublicacionDetailScreen
import app.pawpaws.features.publicar.PublicarScreen
import app.pawpaws.features.comentario.ComentarioScreen
import app.pawpaws.features.configuracion.ConfiguracionScreen
import app.pawpaws.features.mensaje.ChatScreen
import app.pawpaws.features.mensaje.MensajesScreen
import app.pawpaws.features.mensaje.MensajesViewModel
import app.pawpaws.features.notificacion.NotificacionesScreen
import app.pawpaws.features.solicitud.MisSolicitudesScreen
import app.pawpaws.features.solicitud.SolicitudesRecibidasScreen

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

object MainRoutes {
    const val FEED              = "feed"
    const val MAPA              = "mapa"
    const val PUBLICAR          = "publicar"
    const val MENSAJES          = "mensajes"
    const val PERFIL            = "perfil"
    const val PUBLICACION_DETALLE = "publicacion/{id}"

    const val COMENTARIOS         = "comentarios/{id}"

    const val NOTIFICACIONES      = "notificaciones"

    const val SOLICITUDES_RECIBIDAS = "solicitudes_recibidas"

    const val MIS_SOLICITUDES       = "mis_solicitudes"

    const val CONFIGURACION = "configuracion"

    const val CHAT = "chat/{chatId}"

    const val EDITAR_PUBLICACION = "editar_publicacion/{id}"

    fun chatRoute(chatId: String) = "chat/$chatId"

    fun detalleRoute(id: String) = "publicacion/$id"

    fun comentariosRoute(id: String)  = "comentarios/$id"

    fun editarRoute(id: String) = "editar_publicacion/$id"

}

@Composable
fun MainScreen(
    sessionViewModel: SessionViewModel,
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()

    val items = listOf(
        BottomNavItem("Inicio",   Icons.Default.Home,       MainRoutes.FEED),
        BottomNavItem("Mapa",     Icons.Default.LocationOn, MainRoutes.MAPA),
        BottomNavItem("Publicar", Icons.Default.Add,        MainRoutes.PUBLICAR),
        BottomNavItem("Mensajes", Icons.Default.Message,    MainRoutes.MENSAJES),
        BottomNavItem("Perfil",   Icons.Default.Person,     MainRoutes.PERFIL)
    )

    // Rutas donde NO mostramos el bottom bar
    val sinBottomBar = listOf(
        MainRoutes.PUBLICACION_DETALLE.substringBefore("{"),
        MainRoutes.COMENTARIOS.substringBefore("{"),
        "chat/"
    )
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
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
                            onClick = {
                                bottomNavController.navigate(item.route) {
                                    popUpTo(MainRoutes.FEED) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    tint = if (selected) PawBlue else PawDarkText.copy(alpha = 0.5f)
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    fontSize = androidx.compose.ui.unit.TextUnit(
                                        10f,
                                        androidx.compose.ui.unit.TextUnitType.Sp
                                    ),
                                    color = if (selected) PawBlue else PawDarkText.copy(alpha = 0.5f)
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
            navController = bottomNavController,
            startDestination = MainRoutes.FEED,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(MainRoutes.FEED) {
                FeedScreen(
                    onPublicacionClick = { id ->
                        bottomNavController.navigate(MainRoutes.detalleRoute(id))
                    },
                    onComentar = { id ->
                        bottomNavController.navigate(MainRoutes.comentariosRoute(id))
                    },
                    onNotificaciones = {
                        bottomNavController.navigate(MainRoutes.NOTIFICACIONES)
                    },
                    sessionViewModel = sessionViewModel,
                    viewModel = hiltViewModel(),
                )
            }

            composable(MainRoutes.MAPA) {
                MapaScreen(
                    onPublicacionClick = { id ->
                        bottomNavController.navigate(MainRoutes.detalleRoute(id))
                    },
                    viewModel = hiltViewModel()
                )
            }

            composable(MainRoutes.PUBLICAR) {
                PublicarScreen(
                    onPublicacionCreada = {
                        bottomNavController.navigate(MainRoutes.FEED) {
                            popUpTo(MainRoutes.FEED) { inclusive = true }
                        }
                    },
                    onNavigateBack = { bottomNavController.popBackStack() },
                    sessionViewModel = sessionViewModel,
                    viewModel = hiltViewModel()
                )
            }

            composable(MainRoutes.MENSAJES) {
                val viewModel: MensajesViewModel = hiltViewModel()
                MensajesScreen(
                    onChatClick = { chatId ->
                        bottomNavController.navigate(MainRoutes.chatRoute(chatId))
                    },
                    viewModel = viewModel
                )
            }

            composable(
                route     = MainRoutes.CHAT,
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
                val viewModel: MensajesViewModel = hiltViewModel()
                ChatScreen(
                    chatId         = chatId,
                    onNavigateBack = { bottomNavController.popBackStack() },
                    viewModel      = viewModel
                )
            }

            composable(
                route = MainRoutes.EDITAR_PUBLICACION,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                PublicarScreen(
                    onPublicacionCreada = { bottomNavController.popBackStack() },
                    onNavigateBack      = { bottomNavController.popBackStack() },
                    sessionViewModel    = sessionViewModel,
                    idPublicacionEditar = id,
                    viewModel           = hiltViewModel()
                )
            }

            composable(MainRoutes.PERFIL) {
                PerfilScreen(
                    onLogout = {
                        rootNavController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.MAIN) { inclusive = true }
                        }
                    },
                    onPublicacionClick = { id ->
                        bottomNavController.navigate(MainRoutes.detalleRoute(id))
                    },
                    onNuevaPublicacion = {
                        bottomNavController.navigate(MainRoutes.PUBLICAR)
                    },
                    onConfiguracion = {
                        bottomNavController.navigate(MainRoutes.CONFIGURACION) // 👈 AQUÍ
                    },
                    onSolicitudesRecibidas = {
                        bottomNavController.navigate(MainRoutes.SOLICITUDES_RECIBIDAS)
                    },

                    onMisSolicitudes = {
                        bottomNavController.navigate(MainRoutes.MIS_SOLICITUDES)
                    },
                    onEditarPublicacion = { id ->
                        bottomNavController.navigate(MainRoutes.editarRoute(id))
                    },
                    sessionViewModel = sessionViewModel,
                )
            }

            composable(
                route = MainRoutes.PUBLICACION_DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                PublicacionDetailScreen(
                    publicacionId = id,
                    onNavigateBack = { bottomNavController.popBackStack() },
                    onComentar = { bottomNavController.navigate(MainRoutes.comentariosRoute(id)) },
                    sessionViewModel = sessionViewModel,
                    viewModel = hiltViewModel()
                )
            }

            composable(
                route = MainRoutes.COMENTARIOS,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                ComentarioScreen(
                    publicacionId = id,
                    onNavigateBack = { bottomNavController.popBackStack() },
                    sessionViewModel = sessionViewModel,
                    viewModel = hiltViewModel()
                )
            }

            composable(MainRoutes.NOTIFICACIONES) {
                NotificacionesScreen(
                    onNavigateBack = { bottomNavController.popBackStack() },
                    viewModel      = hiltViewModel()
                )
            }

            composable(MainRoutes.SOLICITUDES_RECIBIDAS) {
                SolicitudesRecibidasScreen(
                    onNavigateBack   = { bottomNavController.popBackStack() },
                    sessionViewModel = sessionViewModel,
                    viewModel        = hiltViewModel()
                )
            }

            composable(MainRoutes.MIS_SOLICITUDES) {
                MisSolicitudesScreen(
                    onNavigateBack   = { bottomNavController.popBackStack() },
                    sessionViewModel = sessionViewModel,
                    viewModel        = hiltViewModel()
                )
            }

            composable(MainRoutes.CONFIGURACION) {
                ConfiguracionScreen(
                    onBack = { bottomNavController.popBackStack() }
                )
            }
        }
    }
}