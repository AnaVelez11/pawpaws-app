package app.pawpaws.features.publicacion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import app.pawpaws.core.theme.PawLinkBlue
import app.pawpaws.core.theme.PawOrange
import app.pawpaws.core.utils.RequestResult
import coil.compose.AsyncImage
import app.pawpaws.core.utils.extensions.tipoBadgeColor
import app.pawpaws.core.utils.extensions.tipoLabel
import app.pawpaws.core.utils.resources.ImageResources
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send

@Composable
fun PublicacionDetailScreen(
    publicacionId: String,
    onNavigateBack: () -> Unit,
    onComentar: () -> Unit,
    sessionViewModel: SessionViewModel,
    viewModel: PublicacionDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(publicacionId) { viewModel.cargar(publicacionId) }

    val publicacion    by viewModel.publicacion.collectAsState()
    val mascota        by viewModel.mascota.collectAsState()
    val propietario    by viewModel.propietario.collectAsState()
    val ubicacion      by viewModel.ubicacion.collectAsState()
    val fotos          by viewModel.fotos.collectAsState()
    val solicitudResult by viewModel.solicitudResult.collectAsState()
    val sessionState   by sessionViewModel.sessionState.collectAsState()
    val comentarios       by viewModel.comentarios.collectAsState()
    val textoComentario   by viewModel.textoComentario.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mensajeSolicitud by remember { mutableStateOf("") }

    LaunchedEffect(solicitudResult) {
        when (solicitudResult) {
            is RequestResult.Success -> {
                snackbarHostState.showSnackbar(context.getString(R.string.publicacion_detail_solicitud_enviada_exito))
                viewModel.resetSolicitudResult()
                mostrarDialogo = false
            }
            is RequestResult.Error -> {
                snackbarHostState.showSnackbar((solicitudResult as RequestResult.Error).message)
                viewModel.resetSolicitudResult()
            }
            else -> Unit
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->

        if (publicacion == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PawOrange)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // Imagen principal
            Box {
                AsyncImage(
                    model = fotos.firstOrNull()?.url
                        ?: "https://picsum.photos/400/300?random=${publicacionId.hashCode()}",
                    contentDescription = stringResource(R.string.publicacion_detail_desc_imagen_principal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentScale = ContentScale.Crop
                )
                // Botón volver
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.publicacion_detail_desc_volver), tint = Color.White)
                }

                // Badge tipo
                publicacion?.tipoPublicacion?.let { tipo ->
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.BottomStart)
                            .clip(RoundedCornerShape(20.dp))
                            .background(tipoBadgeColor(tipo))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(tipoLabel(tipo), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {

                // Título
                Text(
                    text = publicacion?.titulo ?: "",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PawDarkText
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info mascota en chips
                mascota?.let { m ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip(stringResource(R.string.publicacion_detail_mascota_tipo, m.tipo.name.lowercase().replaceFirstChar { it.uppercase() }))
                        InfoChip(stringResource(R.string.publicacion_detail_mascota_tamanio, m.tamaño))
                        val generoLabel = if (m.genero == "Macho") stringResource(R.string.publicacion_detail_mascota_genero_macho) 
                                         else stringResource(R.string.publicacion_detail_mascota_genero_hembra)
                        InfoChip(generoLabel)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip("🏷️ ${m.raza}")
                        InfoChip("🎂 ${m.edad} año${if (m.edad != 1) "s" else ""}")
                        InfoChip(if (m.vacunasAlDia == true) "💉 Vacunado" else "⚠️ Sin vacunas")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Ubicación
                SectionTitle(stringResource(R.string.publicacion_detail_seccion_ubicacion))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.publicacion_detail_ubicacion_direccion, ubicacion?.direccion ?: stringResource(R.string.publicacion_detail_ubicacion_default)),
                    fontSize = 14.sp,
                    color = PawGrayText
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Mapa simulado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD0E8FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗺️", fontSize = 36.sp)
                        Text(
                            text = ubicacion?.ciudad ?: stringResource(R.string.publicacion_detail_ciudad_default),
                            color = PawBlue,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.publicacion_detail_lat_lng, ubicacion?.latitud ?: 4.53, ubicacion?.longitud ?: -75.68),
                            fontSize = 11.sp,
                            color = PawGrayText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Descripción
                val sobreMascotaLabel = mascota?.nombre ?: stringResource(R.string.publicacion_detail_seccion_sobre_default)
                SectionTitle(stringResource(R.string.publicacion_detail_seccion_sobre_mascota, sobreMascotaLabel))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = publicacion?.descripcion ?: "",
                    fontSize = 15.sp,
                    color = PawGrayText,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Propietario
                SectionTitle(stringResource(R.string.publicacion_detail_seccion_publicado_por))
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = propietario?.fotoPerfil ?: ImageResources.DEFAULT_USER,
                        contentDescription = stringResource(R.string.publicacion_detail_desc_foto_propietario),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = propietario?.nombre ?: stringResource(R.string.publicacion_detail_usuario_default),
                            fontWeight = FontWeight.SemiBold,
                            color = PawDarkText
                        )
                        Text(
                            text = propietario?.email ?: "",
                            fontSize = 12.sp,
                            color = PawGrayText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                SectionTitle(stringResource(R.string.publicacion_detail_seccion_comentarios))
                Spacer(modifier = Modifier.height(10.dp))

                val idUsuario = (sessionState as? SessionState.Authenticated)?.session?.userId ?: ""
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value         = textoComentario,
                        onValueChange = { viewModel.onTextoComentarioChange(it) },
                        placeholder   = { Text(stringResource(R.string.publicacion_detail_comentario_placeholder)) },
                        modifier      = Modifier.weight(1f),
                        shape         = RoundedCornerShape(12.dp),
                        singleLine    = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick  = { viewModel.agregarComentario(idUsuario) },
                        enabled  = textoComentario.isNotBlank()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = stringResource(R.string.publicacion_detail_desc_enviar), tint = PawOrange)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Comentarios con datos de usuario
                val defaultUsuario = stringResource(R.string.publicacion_detail_usuario_default)
                val comentariosUI = comentarios.map { comentario ->
                    val usuario = propietario?.takeIf { it.id == comentario.idUsuario }
                    app.pawpaws.core.utils.extensions.ComentarioUI(
                        id = comentario.id,
                        nombreUsuario = usuario?.nombre ?: defaultUsuario,
                        fotoUsuario = usuario?.fotoPerfil,
                        texto = comentario.texto,
                        fecha = comentario.fecha
                    )
                }

                if (comentarios.isEmpty()) {
                    Text(stringResource(R.string.publicacion_detail_sin_comentarios), color = PawGrayText, fontSize = 13.sp)
                } else {
                    comentariosUI.forEach { comentario ->
                        ComentarioItem(comentario)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Botón solicitud
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Button(
                        onClick = { /* importante */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PawLinkBlue)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.publicacion_detail_boton_importante), color = Color.White)
                    }

                    Button(
                        onClick = { mostrarDialogo = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PawLinkBlue)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.publicacion_detail_boton_adoptar), color = Color.White)
                    }
                }
            }
        }
    }

    // Diálogo solicitud
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text(stringResource(R.string.publicacion_detail_dialogo_titulo), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(stringResource(R.string.publicacion_detail_dialogo_instruccion), color = PawGrayText, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = mensajeSolicitud,
                        onValueChange = { mensajeSolicitud = it },
                        placeholder = { Text(stringResource(R.string.publicacion_detail_dialogo_placeholder)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = (sessionState as? SessionState.Authenticated)?.session?.userId ?: return@Button
                        viewModel.enviarSolicitud(id, mensajeSolicitud)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PawLinkBlue)
                ) { Text(stringResource(R.string.publicacion_detail_dialogo_enviar)) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text(stringResource(R.string.publicacion_detail_dialogo_cancelar)) }
            }
        )
    }
}

@Composable
private fun InfoChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, PawLinkBlue, RoundedCornerShape(20.dp))
            .background(Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(label, fontSize = 12.sp, color = PawDarkText)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PawDarkText)
    Divider(modifier = Modifier.padding(top = 6.dp), color = Color(0xFFEEEEEE))
}

@Composable
private fun ComentarioItem(comentario: app.pawpaws.core.utils.extensions.ComentarioUI) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        if (comentario.fotoUsuario != null) {
            AsyncImage(
                model = comentario.fotoUsuario,
                contentDescription = stringResource(R.string.publicacion_detail_desc_foto_usuario),
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PawBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comentario.nombreUsuario.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF5F5F5))
                .padding(10.dp)
        ) {
            Text(comentario.nombreUsuario, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PawBlue)
            Text(comentario.texto, fontSize = 14.sp, color = PawDarkText)
            Text(comentario.fecha, fontSize = 11.sp, color = PawGrayText)
        }
    }
}
