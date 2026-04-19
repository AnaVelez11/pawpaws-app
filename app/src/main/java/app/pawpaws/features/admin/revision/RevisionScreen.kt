package app.pawpaws.features.admin.revision

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.VerifiedUser
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
import app.pawpaws.core.theme.AdminBgPage
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.core.theme.PawGreen
import app.pawpaws.core.theme.PawRed
import app.pawpaws.core.utils.resources.ImageResources
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionScreen(
    publicacionId: String,
    onNavigateBack: () -> Unit,
    onAprobada: () -> Unit,
    viewModel: RevisionViewModel = hiltViewModel()
) {
    LaunchedEffect(publicacionId) { viewModel.cargar(publicacionId) }

    val publicacion     by viewModel.publicacion.collectAsState()
    val mascota         by viewModel.mascota.collectAsState()
    val propietario     by viewModel.propietario.collectAsState()
    val ubicacion       by viewModel.ubicacion.collectAsState()
    val imagenUrl       by viewModel.imagenUrl.collectAsState()
    val accionRealizada by viewModel.accionRealizada.collectAsState()

    var mostrarRechazo by remember { mutableStateOf(false) }

    LaunchedEffect(accionRealizada) {
        when (accionRealizada) {
            is AccionRevision.Aprobada  -> { viewModel.resetAccion(); onAprobada() }
            is AccionRevision.Rechazada -> { viewModel.resetAccion(); onNavigateBack() }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.revision_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.revision_cd_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = AdminBgPage
    ) { padding ->

        if (publicacion == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PawBlue)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Imagen grande
            AsyncImage(
                model              = imagenUrl,
                contentDescription = publicacion?.titulo,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Info mascota
            SectionCard {
                Text(
                    publicacion?.titulo ?: "",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = PawDarkText
                )
                Spacer(modifier = Modifier.height(12.dp))
                mascota?.let { m ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip(stringResource(R.string.revision_type_label, m.tipo.name.lowercase().replaceFirstChar { it.uppercase() }))
                        InfoChip(stringResource(R.string.revision_size_label, m.tamaño))
                        InfoChip(stringResource(R.string.revision_gender_label, if (m.genero == "Macho") "♂" else "♀", m.genero))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip(stringResource(R.string.revision_breed_label, m.raza ?: ""))
                        InfoChip(stringResource(R.string.revision_age_label, m.edad ?: 0, if ((m.edad ?: 0) != 1) "s" else ""))
                        InfoChip(if (m.vacunasAlDia == true) stringResource(R.string.revision_vaccinated) else stringResource(R.string.revision_not_vaccinated))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.revision_location_label, ubicacion?.ciudad ?: stringResource(R.string.revision_location_fallback)), fontSize = 13.sp, color = PawGrayText)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            SectionCard {
                Text(stringResource(R.string.revision_section_description), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PawDarkText)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    publicacion?.descripcion ?: "",
                    fontSize   = 14.sp,
                    color      = PawGrayText,
                    lineHeight = 21.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Publicador
            SectionCard {
                Text(stringResource(R.string.revision_section_published_by), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PawDarkText)
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model              = propietario?.fotoPerfil ?: ImageResources.DEFAULT_USER,
                        contentDescription = propietario?.nombre,
                        modifier           = Modifier.size(44.dp).clip(CircleShape),
                        contentScale       = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            propietario?.nombre ?: stringResource(R.string.revision_user_fallback),
                            fontWeight = FontWeight.SemiBold,
                            color      = PawDarkText
                        )
                        Text(propietario?.email ?: "", fontSize = 12.sp, color = PawGrayText)
                    }
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = stringResource(R.string.revision_cd_verified),
                        tint     = PawBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick  = { mostrarRechazo = true },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    border   = androidx.compose.foundation.BorderStroke(1.5.dp, PawRed),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = PawRed)
                ) {
                    Text(stringResource(R.string.revision_btn_reject), fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick  = { viewModel.aprobar() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = PawGreen)
                ) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.revision_btn_approve), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // ── Bottom sheet de rechazo ────────────────────────────────────────────────
    if (mostrarRechazo) {
        ModalBottomSheet(
            onDismissRequest = { mostrarRechazo = false },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            RechazoBottomSheetContent(
                onEnviar = { motivo ->
                    mostrarRechazo = false
                    viewModel.rechazar(motivo)
                },
                onCancelar = { mostrarRechazo = false }
            )
        }
    }
}

@Composable
private fun RechazoBottomSheetContent(
    onEnviar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    val motivosPredefinidos = listOf(
        stringResource(R.string.revision_reason_inappropriate_language),
        stringResource(R.string.revision_reason_false_data),
        stringResource(R.string.revision_reason_illegal_sale),
        stringResource(R.string.revision_reason_animal_abuse)
    )
    var motivoSeleccionado by remember { mutableStateOf("") }
    var motivoPersonalizado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            stringResource(R.string.revision_bottom_sheet_title),
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = PawDarkText
        )
        Spacer(modifier = Modifier.height(16.dp))

        motivosPredefinidos.forEach { motivo ->
            val seleccionado = motivoSeleccionado == motivo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = if (seleccionado) 2.dp else 1.dp,
                        color = if (seleccionado) PawRed else AdminBgPage,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(if (seleccionado) PawRed.copy(alpha = 0.06f) else Color.White)
                    .clickable { motivoSeleccionado = motivo }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = seleccionado,
                    onClick  = { motivoSeleccionado = motivo },
                    colors   = RadioButtonDefaults.colors(selectedColor = PawRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(motivo, fontSize = 14.sp, color = PawDarkText)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value         = motivoPersonalizado,
            onValueChange = { motivoPersonalizado = it },
            placeholder   = { Text(stringResource(R.string.revision_placeholder_reason)) },
            modifier      = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape  = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PawRed,
                unfocusedBorderColor = AdminBgPage
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick  = {
                val motivo = motivoPersonalizado.ifBlank { motivoSeleccionado }
                if (motivo.isNotBlank()) onEnviar(motivo)
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = PawRed),
            enabled  = motivoSeleccionado.isNotBlank() || motivoPersonalizado.isNotBlank()
        ) {
            Text(stringResource(R.string.revision_btn_send_rejection), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick  = onCancelar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.revision_btn_cancel), color = PawGrayText)
        }
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun InfoChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, PawBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(label, fontSize = 12.sp, color = PawDarkText)
    }
}