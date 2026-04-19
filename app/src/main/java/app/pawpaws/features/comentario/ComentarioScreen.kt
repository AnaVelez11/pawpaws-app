package app.pawpaws.features.comentario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
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
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.core.utils.extensions.ComentarioUI
import coil.compose.SubcomposeAsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentarioScreen(
    publicacionId: String,
    onNavigateBack: () -> Unit,
    sessionViewModel: SessionViewModel,
    viewModel: ComentarioViewModel = hiltViewModel()
) {
    val sessionState by sessionViewModel.sessionState.collectAsState()
    val comentarios  by viewModel.comentarios.collectAsState()
    var texto by remember { mutableStateOf("") }

    LaunchedEffect(publicacionId) { viewModel.cargarPorPublicacion(publicacionId) }

    val idUsuario = (sessionState as? SessionState.Authenticated)?.session?.userId ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.comentario_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.comentario_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AdminBgPage)
        ) {
            if (comentarios.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.comentario_no_comments), color = PawGrayText, fontSize = 15.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(comentarios, key = { it.id }) { comentarioUi ->
                        ComentarioItem(comentarioUi)
                    }
                }
            }

            // Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    placeholder = { Text(stringResource(R.string.comentario_placeholder)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.agregarComentario(idUsuario, publicacionId, texto)
                        texto = ""
                    },
                    enabled = texto.isNotBlank()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.comentario_send),
                        tint = if (texto.isNotBlank()) PawBlue else PawGrayText
                    )
                }
            }
        }
    }
}

@Composable
private fun ComentarioItem(comentario: ComentarioUI) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // Avatar con fallback si la URL es null o falla la carga
                if (comentario.fotoUsuario != null) {
                    SubcomposeAsyncImage(
                        model = comentario.fotoUsuario,
                        contentDescription = stringResource(R.string.comentario_user_photo),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        loading = { AvatarPlaceholder() },
                        error   = { AvatarPlaceholder() }
                    )
                } else {
                    AvatarPlaceholder()
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text       = comentario.nombreUsuario,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = PawBlue
                    )
                    Text(
                        text    = comentario.fecha,
                        fontSize = 11.sp,
                        color   = PawGrayText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text     = comentario.texto,
                fontSize = 14.sp,
                color    = PawDarkText
            )
        }
    }
}

@Composable
private fun AvatarPlaceholder() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(AdminBgPage),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            tint = PawBlue,
            modifier = Modifier.size(20.dp)
        )
    }
}