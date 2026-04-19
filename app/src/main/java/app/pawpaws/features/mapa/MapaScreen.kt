package app.pawpaws.features.mapa

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import app.pawpaws.R
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.core.theme.PawGreen
import app.pawpaws.core.theme.PawOrange
import app.pawpaws.core.theme.PawRed
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.model.enums.TipoPublicacion
import coil.compose.AsyncImage

@Composable
fun MapaScreen(
    onPublicacionClick: (String) -> Unit,
    viewModel: MapaViewModel = hiltViewModel()
) {
    val publicaciones by viewModel.publicaciones.collectAsState()
    val seleccionada by viewModel.publicacionSeleccionada.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Mapa simulado ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD0E8FF))
        ) {
            // Cuadrícula decorativa para simular calles
            Column(modifier = Modifier.fillMaxSize()) {
                repeat(8) {
                    HorizontalDivider(color = Color(0xFFB0CCE8), thickness = 1.dp)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Emoji central de mapa
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🗺️", fontSize = 64.sp)
                Text(
                    text = stringResource(R.string.mapa_label_simulado),
                    color = PawBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = stringResource(R.string.mapa_default_city),
                    color = PawGrayText,
                    fontSize = 13.sp
                )
            }

            // Marcadores de publicaciones
            val posiciones = listOf(
                0.25f to 0.35f,
                0.65f to 0.28f,
                0.40f to 0.60f,
                0.72f to 0.55f
            )
            publicaciones.take(4).forEachIndexed { index, publicacion ->
                val (xFrac, yFrac) = posiciones.getOrElse(index) { 0.5f to 0.5f }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MarkerPin(
                        publicacion = publicacion,
                        seleccionada = seleccionada?.id == publicacion.id,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.TopStart)
                            .padding(
                                start = (300 * xFrac).dp,
                                top = (500 * yFrac).dp
                            ),
                        onClick = {
                            viewModel.seleccionar(
                                if (seleccionada?.id == publicacion.id) null else publicacion
                            )
                        }
                    )
                }
            }
        }

        // ── Buscador superior ──────────────────────────────────────
        OutlinedTextField(
            value = busqueda,
            onValueChange = { viewModel.onBusquedaChange(it) },
            placeholder = { Text(stringResource(R.string.mapa_search_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            singleLine = true
        )

        // ── Botón ubicación actual ─────────────────────────────────
        FloatingActionButton(
            onClick = { /* simulado */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = if (seleccionada != null) 180.dp else 16.dp),
            containerColor = Color.White,
            contentColor = PawBlue
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = stringResource(R.string.mapa_cd_my_location))
        }

        // ── Card info publicación seleccionada ─────────────────────
        seleccionada?.let { pub ->
            val ubicacion = remember(pub.idUbicacion) { viewModel.ubicacionDe(pub.idUbicacion) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPublicacionClick(pub.id) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = "https://picsum.photos/120/120?random=${pub.id.hashCode()}",
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(tipoBadgeColor(pub.tipoPublicacion))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(tipoLabel(pub.tipoPublicacion), color = Color.White, fontSize = 10.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(pub.titulo, fontWeight = FontWeight.Bold, color = PawDarkText, fontSize = 15.sp)
                        Text(
                            text = stringResource(R.string.mapa_location_prefix, ubicacion?.ciudad ?: stringResource(R.string.mapa_city_default)),
                            fontSize = 12.sp,
                            color = PawGrayText
                        )
                        Text(
                            text = pub.descripcion,
                            fontSize = 12.sp,
                            color = PawGrayText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { viewModel.seleccionar(null) }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.mapa_cd_close), tint = PawGrayText)
                    }
                }
            }
        }
    }
}

@Composable
private fun MarkerPin(
    publicacion: Publicacion,
    seleccionada: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onClick() }
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (seleccionada) PawOrange else tipoBadgeColor(publicacion.tipoPublicacion))
                    .border(
                        width = if (seleccionada) 2.dp else 0.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = tipoEmoji(publicacion.tipoPublicacion),
                    fontSize = if (seleccionada) 18.sp else 14.sp
                )
            }
            // Punta del pin
            Box(
                modifier = Modifier
                    .size(width = 10.dp, height = 6.dp)
                    .background(
                        color = if (seleccionada) PawOrange else tipoBadgeColor(publicacion.tipoPublicacion),
                        shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                    )
            )
        }
    }
}

private fun tipoEmoji(tipo: TipoPublicacion) = when (tipo) {
    TipoPublicacion.ADOPCION   -> "🐾"
    TipoPublicacion.PERDIDO    -> "❓"
    TipoPublicacion.ENCONTRADO -> "✅"
}

@Composable
private fun tipoLabel(tipo: TipoPublicacion) = when (tipo) {
    TipoPublicacion.ADOPCION   -> stringResource(R.string.mapa_type_adopcion)
    TipoPublicacion.PERDIDO    -> stringResource(R.string.mapa_type_perdido)
    TipoPublicacion.ENCONTRADO -> stringResource(R.string.mapa_type_encontrado)
}

private fun tipoBadgeColor(tipo: TipoPublicacion) = when (tipo) {
    TipoPublicacion.ADOPCION   -> PawGreen
    TipoPublicacion.PERDIDO    -> PawRed
    TipoPublicacion.ENCONTRADO -> PawBlue
}