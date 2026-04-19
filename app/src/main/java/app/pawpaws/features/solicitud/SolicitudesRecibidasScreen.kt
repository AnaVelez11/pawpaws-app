package app.pawpaws.features.solicitud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.pawpaws.R
import app.pawpaws.core.navigation.SessionState
import app.pawpaws.core.navigation.SessionViewModel
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.domain.model.enums.EstadoSolicitud
import app.pawpaws.domain.model.models.Solicitud
import app.pawpaws.features.perfil.PerfilViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesRecibidasScreen(
    onNavigateBack: () -> Unit,
    sessionViewModel: SessionViewModel,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val sessionState        by sessionViewModel.sessionState.collectAsState()
    val solicitudesRecibidas by viewModel.solicitudesRecibidas.collectAsState()

    var tabSeleccionado by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.solicitudes_recibidas_tab_pending),
        stringResource(R.string.solicitudes_recibidas_tab_accepted),
        stringResource(R.string.solicitudes_recibidas_tab_rejected)
    )

    LaunchedEffect(sessionState) {
        if (sessionState is SessionState.Authenticated)
            viewModel.cargar((sessionState as SessionState.Authenticated).session.userId)
    }

    val filtradas = when (tabSeleccionado) {
        0 -> solicitudesRecibidas.filter { it.estado == EstadoSolicitud.PENDIENTE }
        1 -> solicitudesRecibidas.filter { it.estado == EstadoSolicitud.ACEPTADA }
        2 -> solicitudesRecibidas.filter { it.estado == EstadoSolicitud.RECHAZADA }
        else -> solicitudesRecibidas
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.solicitudes_recibidas_title), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.solicitudes_recibidas_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Sub-tabs
            TabRow(
                selectedTabIndex = tabSeleccionado,
                containerColor   = Color.White,
                contentColor     = PawBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[tabSeleccionado]),
                        color    = PawBlue,
                        height   = 2.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, label ->
                    val count = when (index) {
                        0 -> solicitudesRecibidas.count { it.estado == EstadoSolicitud.PENDIENTE }
                        1 -> solicitudesRecibidas.count { it.estado == EstadoSolicitud.ACEPTADA }
                        2 -> solicitudesRecibidas.count { it.estado == EstadoSolicitud.RECHAZADA }
                        else -> 0
                    }
                    Tab(
                        selected = tabSeleccionado == index,
                        onClick  = { tabSeleccionado = index },
                        text     = {
                            Text(
                                text       = if (count > 0) "$label ($count)" else label,
                                fontSize   = 13.sp,
                                fontWeight = if (tabSeleccionado == index) FontWeight.Bold else FontWeight.Normal,
                                color      = if (tabSeleccionado == index) PawBlue else PawGrayText
                            )
                        }
                    )
                }
            }

            if (filtradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.solicitudes_recibidas_empty), color = PawGrayText, fontSize = 15.sp)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtradas, key = { it.id }) { sol ->
                        SolicitudRecibidaCard(
                            solicitud = sol,
                            onAceptar  = { viewModel.actualizarEstadoSolicitud(sol.id, EstadoSolicitud.ACEPTADA) },
                            onRechazar = { viewModel.actualizarEstadoSolicitud(sol.id, EstadoSolicitud.RECHAZADA) },
                            viewModel  = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SolicitudRecibidaCard(
    solicitud: Solicitud,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit,
    viewModel: PerfilViewModel
) {
    val solicitante = remember(solicitud.idSolicitante) {
        viewModel.findUsuario(solicitud.idSolicitante)
    }
    val publicacion = remember(solicitud.idPublicacion) {
        viewModel.findPublicacion(solicitud.idPublicacion)
    }

    val estadoColor = when (solicitud.estado) {
        EstadoSolicitud.PENDIENTE -> Color(0xFFFFA726)
        EstadoSolicitud.ACEPTADA  -> Color(0xFF4CAF50)
        EstadoSolicitud.RECHAZADA -> Color(0xFFE53935)
    }
    val estadoLabel = when (solicitud.estado) {
        EstadoSolicitud.PENDIENTE -> stringResource(R.string.solicitudes_recibidas_status_pending)
        EstadoSolicitud.ACEPTADA  -> stringResource(R.string.solicitudes_recibidas_status_accepted)
        EstadoSolicitud.RECHAZADA -> stringResource(R.string.solicitudes_recibidas_status_rejected)
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Publicación + estado
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model              = viewModel.resolverImagenPublicacion(solicitud.idPublicacion),
                    contentDescription = publicacion?.titulo,
                    modifier           = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = publicacion?.titulo ?: stringResource(R.string.solicitudes_recibidas_pub_fallback),
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = PawDarkText
                    )
                    publicacion?.tipoPublicacion?.let { tipo ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PawBlue.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text       = tipo.name,
                                fontSize   = 10.sp,
                                color      = PawBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(estadoColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(estadoLabel, color = estadoColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))

            // Solicitante
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model              = solicitante?.fotoPerfil,
                    contentDescription = solicitante?.nombre,
                    modifier           = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        solicitante?.nombre ?: stringResource(R.string.solicitudes_recibidas_user_fallback),
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        color      = PawDarkText
                    )
                    Text(stringResource(R.string.solicitudes_recibidas_user_level), fontSize = 11.sp, color = PawGrayText)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★", color = Color(0xFFFFC107), fontSize = 14.sp)
                    Text(stringResource(R.string.solicitudes_recibidas_rating), fontSize = 13.sp, color = PawDarkText, fontWeight = FontWeight.Medium)
                }
            }

            // Botones solo si está pendiente
            if (solicitud.estado == EstadoSolicitud.PENDIENTE) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick  = onRechazar,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = PawDarkText)
                    ) { Text(stringResource(R.string.solicitudes_recibidas_btn_reject)) }

                    Button(
                        onClick  = { onAceptar() },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = PawBlue)
                    ) { Text(stringResource(R.string.solicitudes_recibidas_btn_accept)) }
                }
            }
        }
    }
}
