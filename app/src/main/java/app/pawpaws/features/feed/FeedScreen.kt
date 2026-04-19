package app.pawpaws.features.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.pawpaws.R
import app.pawpaws.core.navigation.SessionState
import app.pawpaws.core.navigation.SessionViewModel
import app.pawpaws.core.theme.AdminBgPage
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.core.theme.PawOrange
import app.pawpaws.domain.model.enums.TipoPublicacion
import app.pawpaws.features.notificacion.NotificacionViewModel
import app.pawpaws.features.publicacion.CardMode
import app.pawpaws.features.publicacion.PublicacionCard
import coil.compose.AsyncImage

@Composable
fun FeedScreen(
    onPublicacionClick: (String) -> Unit,
    onNotificaciones: () -> Unit,
    onComentar: (String) -> Unit,
    sessionViewModel: SessionViewModel,
    viewModel: FeedViewModel = hiltViewModel(),
    notificacionViewModel: NotificacionViewModel = hiltViewModel(),

) {
    val publicaciones       by viewModel.publicaciones.collectAsState()
    val filtroActivo        by viewModel.filtroActivo.collectAsState()
    val query               by viewModel.query.collectAsState()
    val sessionState        by sessionViewModel.sessionState.collectAsState()
    val notificaciones      by notificacionViewModel.notificaciones.collectAsState()

    var mostrarBuscador     by remember { mutableStateOf(false) }

    val nombreUsuario = remember(sessionState) {
        if (sessionState is SessionState.Authenticated)
            viewModel.nombreUsuario((sessionState as SessionState.Authenticated).session.userId)
        else "Usuario"
    }
    val fotoUsuario = remember(sessionState) {
        if (sessionState is SessionState.Authenticated)
            viewModel.fotoUsuario((sessionState as SessionState.Authenticated).session.userId)
        else null
    }

    val listaFiltrada = remember(publicaciones, filtroActivo, query) {
        viewModel.publicacionesFiltradas()
    }

    Column(modifier = Modifier.fillMaxSize().background(AdminBgPage)) {

        val filtros = listOf(
            null to stringResource(R.string.feed_filter_all),
            TipoPublicacion.ADOPCION to stringResource(R.string.feed_filter_adoption),
            TipoPublicacion.PERDIDO to stringResource(R.string.feed_filter_lost),
            TipoPublicacion.ENCONTRADO to stringResource(R.string.feed_filter_found)
        )

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = fotoUsuario ?: app.pawpaws.core.utils.resources.ImageResources.USER_ANA,
                contentDescription = stringResource(R.string.feed_profile_photo),
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { mostrarBuscador = !mostrarBuscador }) {
                    Icon(
                        if (mostrarBuscador) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = stringResource(R.string.feed_search),
                        tint = if (mostrarBuscador) PawOrange else PawGrayText
                    )
                }
                BadgedBox(
                    badge = {
                        if (notificaciones.any{!it.leida}) {
                            Badge(containerColor = PawOrange) {
                                Text(
                                    text = notificaciones.size.coerceAtMost(99).toString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    IconButton(onClick = onNotificaciones) {
                        Icon(Icons.Default.Notifications, contentDescription = stringResource(R.string.feed_notifications), tint = PawGrayText)
                    }
                }
            }
        }

        // Buscador (expandible)
        if (mostrarBuscador) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                placeholder = { Text(stringResource(R.string.feed_search_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = PawGrayText)
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.feed_clear), tint = PawGrayText)
                        }
                    }
                }
            )
        }

        // Saludo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 20.dp, bottom = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.feed_greeting, nombreUsuario.split(" ").first()),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PawGrayText
            )
        }

        // Filtros
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtros) { (tipo, label) ->
                val seleccionado = filtroActivo == tipo
                FilterChip(
                    selected = seleccionado,
                    onClick = { viewModel.setFiltro(tipo) },
                    label = { Text(label, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PawBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Lista
        if (listaFiltrada.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.feed_no_publications), color = PawGrayText)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(listaFiltrada, key = { it.id }) { publicacion ->
                    val cardData = remember(publicacion) { viewModel.toCardData(publicacion) }
                    PublicacionCard(
                        data  = cardData,
                        mode  = CardMode.FEED,
                        onClick = { onPublicacionClick(publicacion.id) },
                        onComentar = { onPublicacionClick(publicacion.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}