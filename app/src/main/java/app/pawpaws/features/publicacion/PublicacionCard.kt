package app.pawpaws.features.publicacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pawpaws.core.theme.PawAmber
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.core.theme.PawGreen
import app.pawpaws.core.theme.PawRed
import app.pawpaws.domain.model.enums.EstadoPublicacion
import app.pawpaws.domain.model.enums.TipoPublicacion
import coil.compose.AsyncImage
import app.pawpaws.R

enum class CardMode { FEED, PERFIL }

data class PublicacionCardData(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val tipoPublicacion: TipoPublicacion,
    val estado: EstadoPublicacion,
    val imagenUrl: String,
    val ciudad: String,
    val nombreDueño: String,
    val fechaCreacion: String
)

@Composable
fun PublicacionCard(
    data: PublicacionCardData,
    mode: CardMode,
    onClick: () -> Unit,
    onEditar: (() -> Unit)? = null,
    onEliminar: (() -> Unit)? = null,
    onComentar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (mode == CardMode.FEED) 3.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        when (mode) {
            CardMode.FEED   -> FeedCardContent(data)
            CardMode.PERFIL -> PerfilCardContent(data, onEditar, onEliminar)
        }
    }
}

// ── FEED layout ───────────────────────────────────────────────────────────────

@Composable
private fun FeedCardContent(data: PublicacionCardData, onComentar: (() -> Unit)? = null ) {
    Column {
        Box {
            AsyncImage(
                model = data.imagenUrl,
                contentDescription = data.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            TipoBadge(
                tipo = data.tipoPublicacion,
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopStart)
            )
        }
        Column(modifier = Modifier.padding(14.dp)) {
            Text(data.titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PawDarkText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = data.descripcion,
                fontSize = 13.sp, color = PawGrayText,
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.publicacion_card_ciudad, data.ciudad), fontSize = 12.sp, color = PawGrayText)
                Text(
                    text = stringResource(R.string.publicacion_card_por, data.nombreDueño), fontSize = 12.sp, color = PawGrayText)
            }

            onComentar?.let {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = it,
                    colors  = ButtonDefaults.textButtonColors(contentColor = PawBlue)
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.publicacion_card_comentar), fontSize = 12.sp)                }
            }

        }

    }
}


// ── PERFIL layout ─────────────────────────────────────────────────────────────

@Composable
private fun PerfilCardContent(
    data: PublicacionCardData,
    onEditar: (() -> Unit)?,
    onEliminar: (() -> Unit)?,
    onComentar: (() -> Unit)? = null
) {
    val estadoColor = estadoColor(data.estado)

    Column {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = data.imagenUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(data.titulo, fontWeight = FontWeight.SemiBold, color = PawDarkText, fontSize = 14.sp)
                Text(tipoLabel(data.tipoPublicacion), fontSize = 12.sp, color = PawBlue)
                Text(stringResource(R.string.publicacion_card_ciudad, data.ciudad), fontSize = 11.sp, color = PawGrayText)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(estadoColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    estadoLabel(data.estado),
                    color = estadoColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Botones editar / eliminar solo en modo PERFIL
        if (onEditar != null || onEliminar != null) {
            Divider(color = Color(0xFFF0F0F0))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.End
            ) {
                onEditar?.let {
                    TextButton(
                        onClick = it,
                        colors = ButtonDefaults.textButtonColors(contentColor = PawBlue)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.publicacion_card_editar), fontSize = 12.sp)                    }
                }
                onEliminar?.let {
                    TextButton(
                        onClick = it,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE53935))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.publicacion_card_eliminar), fontSize = 12.sp)                    }
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun TipoBadge(tipo: TipoPublicacion, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(tipoBadgeColor(tipo))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(tipoLabel(tipo), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun tipoLabel(tipo: TipoPublicacion) = when (tipo) {
    TipoPublicacion.ADOPCION   -> stringResource(R.string.publicacion_card_tipo_adopcion)
    TipoPublicacion.PERDIDO    -> stringResource(R.string.publicacion_card_tipo_perdido)
    TipoPublicacion.ENCONTRADO -> stringResource(R.string.publicacion_card_tipo_encontrado)
}


internal fun tipoBadgeColor(tipo: TipoPublicacion): Color = when (tipo) {
    TipoPublicacion.ADOPCION   -> PawGreen
    TipoPublicacion.PERDIDO    -> PawRed
    TipoPublicacion.ENCONTRADO -> PawBlue
}

internal fun estadoColor(estado: EstadoPublicacion): Color = when (estado) {
    EstadoPublicacion.ACTIVA                  -> PawGreen
    EstadoPublicacion.PAUSADA                 -> PawAmber
    EstadoPublicacion.PENDIENTE_VERIFICACION  -> PawBlue
}

@Composable
internal fun estadoLabel(estado: EstadoPublicacion) = when (estado) {
    EstadoPublicacion.ACTIVA                 -> stringResource(R.string.publicacion_card_estado_activa)
    EstadoPublicacion.PAUSADA                -> stringResource(R.string.publicacion_card_estado_pausada)
    EstadoPublicacion.PENDIENTE_VERIFICACION -> stringResource(R.string.publicacion_card_estado_pendiente)
}