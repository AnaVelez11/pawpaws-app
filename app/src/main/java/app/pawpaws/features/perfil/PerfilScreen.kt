package app.pawpaws.features.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.pawpaws.R
import app.pawpaws.core.navigation.SessionState
import app.pawpaws.core.navigation.SessionViewModel
import app.pawpaws.core.theme.BgCard
import app.pawpaws.core.theme.PawAmber
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.core.theme.PawGrayWhite
import app.pawpaws.core.theme.PawGreen
import app.pawpaws.core.theme.PawLinkBlue
import app.pawpaws.core.theme.PawOrange
import app.pawpaws.core.theme.PawRed
import app.pawpaws.core.theme.PawRedDark
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.core.utils.resources.ImageResources
import app.pawpaws.domain.model.enums.EstadoPublicacion
import app.pawpaws.domain.model.enums.EstadoSolicitud
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.model.models.Solicitud
import app.pawpaws.features.perfil.components.InsigniaChip
import app.pawpaws.features.publicacion.PublicacionCardData
import coil.compose.AsyncImage

@Composable
fun PerfilScreen(
    onLogout: () -> Unit,
    onPublicacionClick: (String) -> Unit,
    onNuevaPublicacion: () -> Unit,
    onConfiguracion: () -> Unit,
    onSolicitudesRecibidas: () -> Unit,
    onMisSolicitudes: () -> Unit,
    onEditarPublicacion: (String) -> Unit,
    sessionViewModel: SessionViewModel,
    viewModel: PerfilViewModel = hiltViewModel(),

) {
    val sessionState         by sessionViewModel.sessionState.collectAsState()
    val usuario              by viewModel.usuario.collectAsState()
    val misPublicaciones     by viewModel.misPublicaciones.collectAsState()
    val solicitudesRecibidas by viewModel.solicitudesRecibidas.collectAsState()
    val logoutResult         by viewModel.logoutResult.collectAsState()
    val isLoading            by viewModel.isLoading.collectAsState()
    val errorMessage         by viewModel.errorMessage.collectAsState()
    val nombreEdit           by viewModel.nombreEdit.collectAsState()
    val telefonoEdit         by viewModel.telefonoEdit.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoEditar by remember { mutableStateOf(false) }
    var mostrarDialogoLogout by remember { mutableStateOf(false) }
    var mostrarDialogoEliminarCuenta by remember { mutableStateOf(false) }
    var publicacionAEliminar by remember { mutableStateOf<Publicacion?>(null) }

    LaunchedEffect(sessionState) {
        if (sessionState is SessionState.Authenticated)
            viewModel.cargar((sessionState as SessionState.Authenticated).session.userId)
    }
    LaunchedEffect(logoutResult) {
        if (logoutResult is RequestResult.Success) { viewModel.resetLogout(); onLogout() }
    }
    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BgCard
    ) { padding ->

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PawBlue)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Encabezado ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = usuario?.fotoPerfil ?: ImageResources.USER_ANA,
                        contentDescription = stringResource(R.string.perfil_cd_foto_perfil),
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        usuario?.nombre ?: stringResource(R.string.perfil_usuario_default),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PawLinkBlue
                    )
                    Text(
                        usuario?.email ?: "",
                        fontSize = 13.sp,
                        color = PawLinkBlue.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Grid de métricas ──────────────────────────────────────
            SectionTitle(
                text     = stringResource(R.string.perfil_seccion_metricas),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                val activas      = misPublicaciones.count { it.estado == EstadoPublicacion.ACTIVA }
                val pendientes   = misPublicaciones.count { it.estado == EstadoPublicacion.PENDIENTE_VERIFICACION }
                val completadas  = solicitudesRecibidas.count { it.estado == EstadoSolicitud.ACEPTADA }
                val rechazadas   = solicitudesRecibidas.count { it.estado == EstadoSolicitud.RECHAZADA }

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        MetricaCell(
                            icono  = Icons.Default.CheckCircle,
                            color  = PawLinkBlue,
                            valor  = activas.toString(),
                            label  = stringResource(R.string.perfil_metrica_activas),
                            modifier = Modifier.weight(1f)
                        )
                        MetricaCell(
                            icono  = Icons.Default.HourglassEmpty,
                            color  = PawLinkBlue,
                            valor  = pendientes.toString(),
                            label  = stringResource(R.string.perfil_metrica_pendientes),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        MetricaCell(
                            icono  = Icons.Default.Favorite,
                            color  = PawLinkBlue,
                            valor  = completadas.toString(),
                            label  = stringResource(R.string.perfil_metrica_completadas),
                            modifier = Modifier.weight(1f)
                        )
                        MetricaCell(
                            icono  = Icons.Default.Cancel,
                            color  = PawLinkBlue,
                            valor  = rechazadas.toString(),
                            label  = stringResource(R.string.perfil_metrica_rechazadas),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Mis solicitudes — tabs ─────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick  = { onSolicitudesRecibidas() },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = PawLinkBlue)
                ) {
                    Text(stringResource(R.string.perfil_btn_solicitudes_recibidas), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                OutlinedButton(
                    onClick  = { onMisSolicitudes() },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape    = RoundedCornerShape(20.dp),
                    border   = androidx.compose.foundation.BorderStroke(1.5.dp, PawBlue),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = PawLinkBlue)
                ) {
                    Text(stringResource(R.string.perfil_btn_mis_solicitudes), fontSize = 12.sp)
                }
            }

            // ── Insignias ─────────────────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.perfil_seccion_insignias),
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = PawDarkText,
                    modifier   = Modifier.weight(1f)
                )
                TextButton(onClick = {}) {
                    Text(stringResource(R.string.perfil_btn_ver_todas), color = PawLinkBlue, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.CenterHorizontally)
            ) {
                listOf(
                    Icons.Default.Pets to stringResource(R.string.perfil_icon_pets),
                    Icons.Default.Favorite to stringResource(R.string.perfil_icon_favorite),
                    Icons.Default.VolunteerActivism to stringResource(R.string.perfil_icon_volunteer),
                    Icons.Default.Star to stringResource(R.string.perfil_icon_star),
                ).forEach { (icon, nombre) ->
                    InsigniaChip(icon = icon, nombre = nombre)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Banner estadísticas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape  = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PawLinkBlue.copy(alpha = 0.08f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = null,
                        tint = PawLinkBlue,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        stringResource(R.string.perfil_ver_estadisticas),
                        fontSize = 14.sp,
                        color = PawDarkText,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = PawLinkBlue.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Mis publicaciones ─────────────────────────────────────────
            SectionTitle(
                text     = stringResource(R.string.perfil_seccion_publicaciones),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (misPublicaciones.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape  = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PawLinkBlue.copy(alpha = 0.08f)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(PawLinkBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🐾", fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            stringResource(R.string.perfil_empty_title),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = PawDarkText
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            stringResource(R.string.perfil_empty_desc),
                            fontSize = 12.sp,
                            color = PawGrayText,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onNuevaPublicacion,
                            shape   = RoundedCornerShape(12.dp),
                            colors  = ButtonDefaults.buttonColors(containerColor = PawLinkBlue)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.perfil_btn_crear_publicacion))
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    misPublicaciones.forEach { pub ->
                        val cardData = remember(pub) { viewModel.toCardData(pub) }
                        PublicacionListItem(
                            data    = cardData,
                            onClick = { onPublicacionClick(pub.id) },
                            onEditar   = { onEditarPublicacion(pub.id) },
                            onEliminar = { publicacionAEliminar = pub }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Menú de acciones ──────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column {
                    MenuAccion(
                        icono  = Icons.Default.Edit,
                        texto  = stringResource(R.string.perfil_menu_editar_perfil),
                        color  = PawDarkText,
                        onClick = { mostrarDialogoEditar = true }
                    )
                    MenuAccion(
                        icono  = Icons.Default.Settings,
                        texto  = stringResource(R.string.perfil_menu_configuracion),
                        color  = PawDarkText,
                        onClick = onConfiguracion
                    )
                    MenuAccion(
                        icono  = Icons.Default.ExitToApp,
                        texto  = stringResource(R.string.perfil_menu_cerrar_sesion),
                        color  = PawDarkText,
                        onClick = { mostrarDialogoLogout = true }
                    )
                    MenuAccion(
                        icono  = Icons.Default.DeleteForever,
                        texto  = stringResource(R.string.perfil_menu_eliminar_cuenta),
                        color  = PawRed,
                        onClick = { mostrarDialogoEliminarCuenta = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // ── Diálogo editar perfil ──────────────────────────────────────────────────
    if (mostrarDialogoEditar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEditar = false },
            title = { Text(stringResource(R.string.perfil_dialog_edit_title), fontWeight = FontWeight.Bold) },
            text  = {
                Column {
                    OutlinedTextField(
                        value = nombreEdit.value,
                        onValueChange = { viewModel.onNombreChange(it) },
                        label = { Text(stringResource(R.string.perfil_label_nombre)) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nombreEdit.error != null,
                        supportingText = {
                            nombreEdit.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = telefonoEdit.value,
                        onValueChange = { viewModel.onTelefonoChange(it) },
                        label = { Text(stringResource(R.string.perfil_label_telefono)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.guardarCambios(); mostrarDialogoEditar = false },
                    colors  = ButtonDefaults.buttonColors(containerColor = PawBlue)
                ) { Text(stringResource(R.string.perfil_btn_guardar)) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEditar = false }) { Text(stringResource(R.string.perfil_btn_cancelar)) }
            }
        )
    }

    // ── Diálogo confirmar eliminación de publicación ───────────────────────────
    publicacionAEliminar?.let { pub ->
        AlertDialog(
            onDismissRequest = { publicacionAEliminar = null },
            title = { Text(stringResource(R.string.perfil_dialog_delete_pub_title), fontWeight = FontWeight.Bold) },
            text  = { Text(stringResource(R.string.perfil_dialog_delete_pub_msg, pub.titulo)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.eliminarPublicacion(pub.id); publicacionAEliminar = null },
                    colors  = ButtonDefaults.buttonColors(containerColor = PawRed)
                ) { Text(stringResource(R.string.perfil_btn_eliminar)) }
            },
            dismissButton = {
                TextButton(onClick = { publicacionAEliminar = null }) { Text(stringResource(R.string.perfil_btn_cancelar)) }
            }
        )
    }

    // ── Diálogo cerrar sesión ──────────────────────────────────────────────────
    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            title = { Text(stringResource(R.string.perfil_dialog_logout_title), fontWeight = FontWeight.Bold) },
            text  = { Text(stringResource(R.string.perfil_dialog_logout_msg)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.cerrarSesion() },
                    colors  = ButtonDefaults.buttonColors(containerColor = PawRed)
                ) { Text(stringResource(R.string.perfil_btn_salir)) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLogout = false }) { Text(stringResource(R.string.perfil_btn_cancelar)) }
            }
        )
    }

    // ── Diálogo eliminar cuenta ──────────────────────────────────────────────────
    if (mostrarDialogoEliminarCuenta) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminarCuenta = false },
            title = {
                Text(
                    stringResource(R.string.perfil_dialog_delete_acc_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(stringResource(R.string.perfil_dialog_delete_acc_msg))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        stringResource(R.string.perfil_dialog_delete_acc_alert),
                        color = PawRedDark,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarCuenta()
                        mostrarDialogoEliminarCuenta = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PawRed
                    )
                ) {
                    Text(stringResource(R.string.perfil_btn_eliminar))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoEliminarCuenta = false }
                ) {
                    Text(stringResource(R.string.perfil_btn_cancelar))
                }
            }
        )
    }
}

// ── Componentes privados ───────────────────────────────────────────────────────

@Composable
private fun MetricaCell(
    icono: ImageVector,
    color: Color,
    valor: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PawDarkText)
            Text(label, fontSize = 11.sp, color = PawGrayText)
        }
    }
}

@Composable
private fun TabButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick  = onClick,
        modifier = modifier.height(38.dp),
        shape    = RoundedCornerShape(20.dp),
        colors   = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) PawBlue else Color.Transparent,
            contentColor   = if (selected) Color.White else PawBlue
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = PawBlue
        )
    ) {
        Text(label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun InsigniaChip(emoji: String, nombre: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(PawGrayWhite),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(nombre, fontSize = 10.sp, color = PawGrayText)
    }
}

@Composable
private fun PublicacionListItem(
    data: PublicacionCardData,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val estadoColor = when (data.estado) {
        EstadoPublicacion.ACTIVA                 -> PawGreen
        EstadoPublicacion.PENDIENTE_VERIFICACION -> PawAmber
        EstadoPublicacion.PAUSADA                -> PawOrange
    }
    val estadoLabel = when (data.estado) {
        EstadoPublicacion.ACTIVA                 -> stringResource(R.string.perfil_estado_proceso)
        EstadoPublicacion.PENDIENTE_VERIFICACION -> stringResource(R.string.perfil_estado_pendiente)
        EstadoPublicacion.PAUSADA                -> stringResource(R.string.perfil_estado_completado)
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick   = onClick
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model              = data.imagenUrl,
                contentDescription = data.titulo,
                modifier           = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(data.titulo, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = PawDarkText)
                Text(
                    "${data.tipoPublicacion.name.lowercase().replaceFirstChar { it.uppercase() }} · ${data.fechaCreacion}",
                    fontSize = 12.sp,
                    color    = PawGrayText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(estadoColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        estadoLabel,
                        fontSize   = 10.sp,
                        color      = estadoColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            var expandido by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expandido = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.perfil_cd_opciones), tint = PawGrayText)
                }
                DropdownMenu(
                    expanded = expandido,
                    onDismissRequest = { expandido = false }
                ) {
                    DropdownMenuItem(
                        text    = { Text(stringResource(R.string.perfil_menu_editar)) },
                        onClick = { expandido = false; onEditar() }
                    )
                    DropdownMenuItem(
                        text    = { Text(stringResource(R.string.perfil_menu_eliminar), color = PawRed) },
                        onClick = { expandido = false; onEliminar() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuAccion(
    icono: ImageVector,
    texto: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(texto, fontSize = 15.sp, color = color, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = color.copy(alpha = 0.5f))
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text       = text,
        fontSize   = 16.sp,
        fontWeight = FontWeight.Bold,
        color      = PawDarkText,
        modifier   = modifier
    )
}

@Composable
private fun SolicitudItem(solicitud: Solicitud) {
    val (color, label) = when (solicitud.estado) {
        EstadoSolicitud.PENDIENTE -> PawAmber to stringResource(R.string.perfil_solicitud_pendiente)
        EstadoSolicitud.ACEPTADA  -> PawGreen    to stringResource(R.string.perfil_solicitud_aceptada)
        EstadoSolicitud.RECHAZADA -> PawRed to stringResource(R.string.perfil_solicitud_rechazada)
    }
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(10.dp),
        colors    = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.perfil_solicitud_id, solicitud.id.take(6)),
                    fontWeight = FontWeight.Medium,
                    color      = PawDarkText,
                    fontSize   = 14.sp
                )
                Text(
                    solicitud.mensaje.take(50) + if (solicitud.mensaje.length > 50) "..." else "",
                    fontSize = 12.sp,
                    color    = PawGrayText
                )
                Text(solicitud.fecha, fontSize = 11.sp, color = PawGrayText)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(color.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
