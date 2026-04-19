package app.pawpaws.features.mensaje

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pawpaws.R // Asegúrate de importar tu R
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.core.theme.PawGreen
import app.pawpaws.domain.model.models.Mensaje
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MensajesScreen(
    onChatClick: (String) -> Unit,
    viewModel: MensajesViewModel
) {
    val activos    by viewModel.activos.collectAsState()
    val noLeidos   by viewModel.noLeidos.collectAsState()
    val archivados by viewModel.archivados.collectAsState()

    var tabIndex by remember { mutableIntStateOf(0) }

    // Lista de recursos para las pestañas (Checklist: Labels)
    val tabs = listOf(
        R.string.mensajes_tab_activos,
        R.string.mensajes_tab_sin_leer,
        R.string.mensajes_tab_archivados
    )

    val data = when (tabIndex) {
        0    -> activos
        1    -> noLeidos
        else -> archivados
    }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick            = { /* TODO: nuevo chat */ },
                containerColor     = PawBlue,
                contentColor       = Color.White,
                shape              = RoundedCornerShape(16.dp)
            ) {
                // Checklist: Descripciones (Accesibilidad)
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = stringResource(R.string.mensajes_cd_nuevo_mensaje)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text      = stringResource(R.string.mensajes_title),
                    modifier  = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = PawDarkText
                )
            }

            // Tabs
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor   = Color.White,
                contentColor     = PawBlue,
                indicator = { positions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(positions[tabIndex]),
                        color    = PawBlue,
                        height   = 2.dp
                    )
                }
            ) {
                tabs.forEachIndexed { i, resId ->
                    Tab(
                        selected = tabIndex == i,
                        onClick  = { tabIndex = i },
                        text     = {
                            Text(
                                text       = stringResource(resId),
                                fontSize   = 13.sp,
                                fontWeight = if (tabIndex == i) FontWeight.Bold else FontWeight.Normal,
                                color      = if (tabIndex == i) PawBlue else PawGrayText
                            )
                        }
                    )
                }
            }

            if (data.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Checklist: Mensajes de estado
                    Text(
                        text  = stringResource(R.string.mensajes_empty_state),
                        color = PawGrayText,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(data, key = { it.chatId }) { chat ->
                        ChatItem(
                            chat    = chat,
                            onClick = { onChatClick(chat.chatId) }
                        )
                        HorizontalDivider(
                            color     = Color.Transparent, // O el color de tu tema
                            thickness = 1.dp,
                            modifier  = Modifier.padding(start = 82.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: Mensaje, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (!chat.leido) PawBlue.copy(alpha = 0.05f) else Color.White)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box {
            if (chat.fotoPerfil != null) {
                AsyncImage(
                    model              = chat.fotoPerfil,
                    contentDescription = chat.nombre,
                    modifier           = Modifier
                        .size(52.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(PawBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = chat.nombre.first().uppercase(),
                        color      = PawBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp
                    )
                }
            }
            // Punto online
            Box(
                modifier = Modifier
                    .size(13.dp)
                    .clip(CircleShape)
                    .background(PawGreen)
                    .align(Alignment.BottomEnd)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = chat.nombre,
                fontWeight = if (!chat.leido) FontWeight.Bold else FontWeight.SemiBold,
                fontSize   = 15.sp,
                color      = PawDarkText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text       = chat.ultimoMensaje,
                fontSize   = 13.sp,
                color      = if (!chat.leido) PawDarkText else PawGrayText,
                fontWeight = if (!chat.leido) FontWeight.Medium else FontWeight.Normal,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text     = formatTimestamp(chat.timestamp),
                fontSize = 11.sp,
                color    = if (!chat.leido) PawBlue else PawGrayText
            )
            if (!chat.leido) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(PawBlue)
                )
            }
        }
    }
}

@Composable
private fun formatTimestamp(ts: Long): String {
    val ahora = System.currentTimeMillis()
    val diff  = ahora - ts
    return when {
        // Checklist: Uso de stringResource en lógica de UI
        diff < 60_000          -> stringResource(R.string.mensajes_time_now)
        diff < 3_600_000       -> stringResource(R.string.mensajes_time_minutes, diff / 60_000)
        diff < 86_400_000      -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ts))
        else                   -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(ts))
    }
}