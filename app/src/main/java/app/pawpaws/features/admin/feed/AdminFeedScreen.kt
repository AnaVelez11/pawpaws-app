package app.pawpaws.features.admin.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import app.pawpaws.core.theme.AdminBgPage
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.core.theme.PawRedDark
import app.pawpaws.domain.model.enums.TipoPublicacion
import app.pawpaws.domain.model.models.Publicacion
import coil.compose.AsyncImage
import app.pawpaws.R

@Composable
fun AdminFeedScreen(
    onRevisar: (String) -> Unit,
    viewModel: AdminFeedViewModel = hiltViewModel()
) {
    val publicaciones by viewModel.publicaciones.collectAsState()
    val query by viewModel.query.collectAsState()

    val listaFiltrada = remember(publicaciones, query) { viewModel.filtradas() }

    Scaffold(
        containerColor = AdminBgPage,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_feed_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PawDarkText
                )
                Text(
                    text = stringResource(R.string.admin_feed_count_pending,listaFiltrada.size),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PawGrayText
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Buscador
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.admin_feed_search_placeholder),
                        color = PawGrayText
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null, tint = PawGrayText) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Close, null, tint = PawGrayText)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PawBlue,
                    unfocusedBorderColor = Color(0xFFDDE3ED)
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (listaFiltrada.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.admin_feed_empty) ,
                        color = PawGrayText)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaFiltrada, key = { it.id }) { pub ->
                        PublicacionPendienteCard(
                            publicacion = pub,
                            imagenUrl = viewModel.resolverImagen(pub),
                            nombreUsuario = viewModel.nombreUsuario(pub.idUsuario),
                            fotoUsuario = viewModel.fotoUsuario(pub.idUsuario),
                            ciudad = viewModel.ciudad(pub),
                            onRevisar = { onRevisar(pub.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}
@Composable
private fun PublicacionPendienteCard(
    publicacion: Publicacion,
    imagenUrl: String,
    nombreUsuario: String,
    fotoUsuario: String?,
    ciudad: String,
    onRevisar: () -> Unit
) {
    val tipoBadgeColor = when (publicacion.tipoPublicacion) {
        TipoPublicacion.ADOPCION   -> Color(0xFF4CAF50)
        TipoPublicacion.PERDIDO    -> PawRedDark
        TipoPublicacion.ENCONTRADO -> PawBlue
    }
    val tipoLabel = when (publicacion.tipoPublicacion) {
        TipoPublicacion.ADOPCION   -> stringResource(R.string.tipo_adopcion)
        TipoPublicacion.PERDIDO    -> stringResource(R.string.tipo_perdido)
        TipoPublicacion.ENCONTRADO -> stringResource(R.string.tipo_encontrado)
    }
    val esUrgente = publicacion.tipoPublicacion == TipoPublicacion.PERDIDO

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            // Imagen
            Box {
                AsyncImage(
                    model              = imagenUrl,
                    contentDescription = publicacion.titulo,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                // Badge tipo
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(20.dp))
                        .background(tipoBadgeColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(tipoLabel, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                // Badge urgente
                if (esUrgente) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(20.dp))
                            .background(PawRedDark)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.badge_urgente),
                            color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                // Tiempo
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = stringResource(R.string.time_ago_short), color = Color.White, fontSize = 10.sp)
                }
            }

            // Info
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    publicacion.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = PawDarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("📍 $ciudad", fontSize = 12.sp, color = PawGrayText)

                Spacer(modifier = Modifier.height(10.dp))

                // Usuario + botón
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model              = fotoUsuario,
                        contentDescription = nombreUsuario,
                        modifier           = Modifier
                            .size(30.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        nombreUsuario,
                        fontSize  = 13.sp,
                        color     = PawGrayText,
                        modifier  = Modifier.weight(1f)
                    )
                    Button(
                        onClick  = onRevisar,
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = PawBlue),
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_revisar), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}