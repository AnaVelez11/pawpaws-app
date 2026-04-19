package app.pawpaws.features.solicitud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Message
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
fun MisSolicitudesScreen(
    onNavigateBack: () -> Unit,
    sessionViewModel: SessionViewModel,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val sessionState   by sessionViewModel.sessionState.collectAsState()
    val misSolicitudes by viewModel.misSolicitudes.collectAsState()

    var tabSeleccionado by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.mis_solicitudes_tab_pending),
        stringResource(R.string.mis_solicitudes_tab_accepted),
        stringResource(R.string.mis_solicitudes_tab_rejected)
    )

    LaunchedEffect(sessionState) {
        if (sessionState is SessionState.Authenticated)
            viewModel.cargar((sessionState as SessionState.Authenticated).session.userId)
    }

    val filtradas = when (tabSeleccionado) {
        0 -> misSolicitudes.filter { it.estado == EstadoSolicitud.PENDIENTE }
        1 -> misSolicitudes.filter { it.estado == EstadoSolicitud.ACEPTADA }
        2 -> misSolicitudes.filter { it.estado == EstadoSolicitud.RECHAZADA }
        else -> misSolicitudes
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.mis_solicitudes_title), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.mis_solicitudes_back))
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
                        0 -> misSolicitudes.count { it.estado == EstadoSolicitud.PENDIENTE }
                        1 -> misSolicitudes.count { it.estado == EstadoSolicitud.ACEPTADA }
                        2 -> misSolicitudes.count { it.estado == EstadoSolicitud.RECHAZADA }
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
                    Text(stringResource(R.string.mis_solicitudes_empty), color = PawGrayText, fontSize = 15.sp)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtradas, key = { it.id }) { sol ->
                        MiSolicitudCard(solicitud = sol, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun MiSolicitudCard(
    solicitud: Solicitud,
    viewModel: PerfilViewModel
) {
    val publicacion = remember(solicitud.idPublicacion) {
        viewModel.findPublicacion(solicitud.idPublicacion)
    }
    val propietario = remember(solicitud.idPropietario) {
        viewModel.findUsuario(solicitud.idPropietario)
    }

    val estadoColor = when (solicitud.estado) {
        EstadoSolicitud.PENDIENTE -> Color(0xFFFFA726)
        EstadoSolicitud.ACEPTADA  -> Color(0xFF4CAF50)
        EstadoSolicitud.RECHAZADA -> Color(0xFFE53935)
    }
    val estadoLabel = when (solicitud.estado) {
        EstadoSolicitud.PENDIENTE -> stringResource(R.string.mis_solicitudes_status_pending)
        EstadoSolicitud.ACEPTADA  -> stringResource(R.string.mis_solicitudes_status_accepted)
        EstadoSolicitud.RECHAZADA -> stringResource(R.string.mis_solicitudes_status_rejected)
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            // Imagen de la publicación
            AsyncImage(
                model              = viewModel.resolverImagenPublicacion(solicitud.idPublicacion),
                contentDescription = publicacion?.titulo,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {

                // Título + ubicación + fecha
                Text(
                    text       = publicacion?.titulo ?: stringResource(R.string.mis_solicitudes_pub_fallback),
                    fontWeight = FontWeight.Bold,
                    fontSize   = 17.sp,
                    color      = PawDarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍 ", fontSize = 12.sp)
                    Text(
                        text     = stringResource(R.string.mis_solicitudes_location_time),
                        fontSize = 12.sp,
                        color    = PawGrayText
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Propietario
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF5F7FA))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            stringResource(R.string.mis_solicitudes_published_by),
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color      = PawGrayText,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDDE8FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👤", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    propietario?.nombre ?: stringResource(R.string.mis_solicitudes_user_fallback),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 14.sp,
                                    color      = PawDarkText
                                )
                                Text(
                                    stringResource(R.string.mis_solicitudes_user_stats),
                                    fontSize = 11.sp,
                                    color    = PawGrayText
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Badge de estado
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint     = estadoColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        estadoLabel,
                        color      = estadoColor,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Botón ir a conversación — solo si fue aceptada
                if (solicitud.estado == EstadoSolicitud.ACEPTADA) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick  = { /* TODO: navegar a chat */ },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = PawBlue)
                    ) {
                        Icon(
                            Icons.Default.Message,
                            contentDescription = null,
                            tint     = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.mis_solicitudes_btn_chat), color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
