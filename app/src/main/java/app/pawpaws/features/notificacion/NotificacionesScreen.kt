package app.pawpaws.features.notificacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.pawpaws.R
import app.pawpaws.core.theme.Divider
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.domain.model.models.Notificacion

private val NotifBackground = Color(0xFFF0F4FF)
private val NotifIconBg     = Color(0xFFDDE8FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificacionViewModel = hiltViewModel()
) {
    val notificaciones by viewModel.notificaciones.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.notificaciones_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PawDarkText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.notificaciones_back), tint = PawDarkText)
                    }
                },
                actions = {
                    // Marca todas como leídas al entrar
                    LaunchedEffect(Unit) { viewModel.marcarTodasLeidas() }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->

        if (notificaciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.notificaciones_empty), color = PawGrayText, fontSize = 15.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(notificaciones, key = { it.id }) { notif ->
                    NotificacionItem(
                        notificacion = notif,
                        onVisto      = { viewModel.marcarComoLeida(notif.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificacionItem(
    notificacion: Notificacion,
    onVisto: () -> Unit
) {
    val icono: ImageVector = when {
        notificacion.titulo.contains("perdida", ignoreCase = true) ||
                notificacion.titulo.contains("perdido", ignoreCase = true) -> Icons.Default.LocationOn
        else -> Icons.Default.Pets
    }

    val fondoItem = if (!notificacion.leida) NotifBackground else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(fondoItem)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Ícono circular
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(NotifIconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = PawBlue,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Contenido
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = notificacion.titulo,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                color      = PawDarkText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = notificacion.descripcion,
                fontSize = 13.sp,
                color    = PawGrayText,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text      = notificacion.fecha,
                fontSize  = 11.sp,
                color     = PawBlue,
                modifier  = Modifier.align(Alignment.End)
            )
        }
    }

    HorizontalDivider(color = Divider, thickness = 1.dp)
}
