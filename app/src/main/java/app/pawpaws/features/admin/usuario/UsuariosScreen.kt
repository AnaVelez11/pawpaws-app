package app.pawpaws.features.admin.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.pawpaws.R
import app.pawpaws.core.theme.*
import app.pawpaws.domain.model.enums.EstadoUsuario
import app.pawpaws.domain.model.models.Usuario
import coil.compose.AsyncImage

// ── Lista de usuarios ─────────────────────────────────────────────────────────

@Composable
fun UsuariosScreen(
    onVerPerfil: (String) -> Unit,
    viewModel: UsuariosViewModel = hiltViewModel()
) {
    val usuarios  by viewModel.usuarios.collectAsState()
    val query     by viewModel.query.collectAsState()
    val filtrados = remember(usuarios, query) { viewModel.filtrados() }

    Scaffold(
        containerColor = AdminBgPage,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(stringResource(R.string.usuarios_title), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PawDarkText)
                Text(stringResource(R.string.usuarios_registered_count, usuarios.size), fontSize = 13.sp, color = PawGrayText)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            OutlinedTextField(
                value         = query,
                onValueChange = { viewModel.onQueryChange(it) },
                placeholder   = { Text(stringResource(R.string.usuarios_search_placeholder), color = PawGrayText) },
                modifier      = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape         = RoundedCornerShape(24.dp),
                singleLine    = true,
                leadingIcon   = { Icon(Icons.Default.Search, null, tint = PawGrayText) },
                trailingIcon  = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Close, null, tint = PawGrayText)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = PawBlue,
                    unfocusedBorderColor = AdminBgPage
                )
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtrados, key = { it.id }) { usuario ->
                    UsuarioCard(usuario = usuario, onVerPerfil = { onVerPerfil(usuario.id) })
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun UsuarioCard(usuario: Usuario, onVerPerfil: () -> Unit) {
    val estadoColor = when (usuario.estado) {
        EstadoUsuario.ACTIVO     -> PawGreen
        EstadoUsuario.SUSPENDIDO -> PawRed
        EstadoUsuario.INACTIVO   -> PawGrayText
    }
    val estadoLabel = when (usuario.estado) {
        EstadoUsuario.ACTIVO     -> stringResource(R.string.usuarios_status_active)
        EstadoUsuario.SUSPENDIDO -> stringResource(R.string.usuarios_status_suspended)
        EstadoUsuario.INACTIVO   -> stringResource(R.string.usuarios_status_inactive)
    }
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model              = usuario.fotoPerfil,
                contentDescription = usuario.nombre,
                modifier           = Modifier.size(48.dp).clip(CircleShape),
                contentScale       = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = PawDarkText)
                Text(usuario.email, fontSize = 12.sp, color = PawGrayText)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(estadoColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(estadoLabel, color = estadoColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            TextButton(onClick = onVerPerfil) {
                Text(stringResource(R.string.usuarios_view_profile), color = PawBlue, fontSize = 13.sp)
            }
        }
    }
}

// ── Perfil detallado de usuario (vista admin) ─────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioAdminScreen(
    usuarioId: String,
    onNavigateBack: () -> Unit,
    viewModel: UsuariosViewModel = hiltViewModel()
) {
    val usuarios by viewModel.usuarios.collectAsState()
    val usuario  = usuarios.firstOrNull { it.id == usuarioId }

    // ── Estados de los modales ─────────────────────────────────────────────
    var showAdvertencia          by remember { mutableStateOf(false) }
    var showSuspension           by remember { mutableStateOf(false) }
    var showSuspensionPermanente by remember { mutableStateOf(false) }

    // ── Modales ────────────────────────────────────────────────────────────
    if (showAdvertencia) {
        AdvertenciaDialog(
            usuarioId   = usuarioId,
            onDismiss   = { showAdvertencia = false },
            onConfirmar = { motivo, mensaje ->
                viewModel.advertir(usuarioId, motivo, mensaje)
                showAdvertencia = false
            }
        )
    }

    if (showSuspension) {
        SuspensionTemporalDialog(
            usuarioId   = usuarioId,
            onDismiss   = { showSuspension = false },
            onConfirmar = { duracion, motivo, detalles ->
                viewModel.suspender(usuarioId, duracion, motivo, detalles)
                showSuspension = false
            }
        )
    }

    if (showSuspensionPermanente) {
        SuspensionPermanenteDialog(
            usuarioId   = usuarioId,
            onDismiss   = { showSuspensionPermanente = false },
            onConfirmar = { motivo, justificacion ->
                viewModel.suspenderPermanente(usuarioId, motivo, justificacion)
                showSuspensionPermanente = false
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.usuarios_profile_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.usuarios_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = AdminBgPage
    ) { padding ->

        if (usuario == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.usuarios_user_not_found), color = PawGrayText)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Card(
                modifier  = Modifier.fillMaxWidth().padding(16.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model              = usuario.fotoPerfil,
                        contentDescription = usuario.nombre,
                        modifier           = Modifier.size(80.dp).clip(CircleShape),
                        contentScale       = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(usuario.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PawDarkText)
                    Text(usuario.email, fontSize = 13.sp, color = PawGrayText)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(stringResource(R.string.usuarios_trust_level), fontSize = 13.sp, color = PawBlue)
                }
            }

            // Stats
            Card(
                modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("3", stringResource(R.string.usuarios_pets))
                    StatItem("5", stringResource(R.string.usuarios_posts))
                    StatItem("1", stringResource(R.string.usuarios_reports))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contacto
            Card(
                modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.usuarios_contact_data), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PawDarkText)
                    Spacer(modifier = Modifier.height(10.dp))
                    ContactItem(Icons.Default.Phone, usuario.telefono ?: stringResource(R.string.usuarios_not_registered))
                    ContactItem(Icons.Default.Email, usuario.email)
                    ContactItem(Icons.Default.Badge, stringResource(R.string.usuarios_role, usuario.rol.name))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Acciones — cada botón abre su modal ───────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick  = { showAdvertencia = true },          // ← abre modal
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = PawAmber)
                ) {
                    Icon(Icons.Default.Warning, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.usuarios_send_warning), fontWeight = FontWeight.Medium)
                }

                OutlinedButton(
                    onClick  = { showSuspension = true },           // ← abre modal
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(12.dp),
                    border   = androidx.compose.foundation.BorderStroke(1.5.dp, PawRed),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = PawRed)
                ) {
                    Icon(Icons.Default.Block, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.usuarios_suspend_temporal), fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick  = { showSuspensionPermanente = true }, // ← abre modal
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = PawRed)
                ) {
                    Icon(Icons.Default.DeleteForever, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.usuarios_suspend_permanent), fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// ── Helpers privados ───────────────────────────────────────────────────────────

@Composable
private fun StatItem(valor: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PawBlue)
        Text(label, fontSize = 11.sp, color = PawGrayText)
    }
}

@Composable
private fun ContactItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = PawBlue, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 13.sp, color = PawGrayText)
    }
}