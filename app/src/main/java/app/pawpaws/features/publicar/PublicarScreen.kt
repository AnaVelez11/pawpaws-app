package app.pawpaws.features.publicar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import app.pawpaws.R
import app.pawpaws.core.navigation.SessionState
import app.pawpaws.core.navigation.SessionViewModel
import app.pawpaws.core.theme.AdminBgPage
import app.pawpaws.core.theme.PawAmber
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText
import app.pawpaws.core.theme.PawGrayText
import app.pawpaws.core.theme.PawGrayWhite
import app.pawpaws.core.theme.PawLinkBlue
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.domain.model.enums.TipoMascota
import app.pawpaws.domain.model.enums.TipoPublicacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarScreen(
    onPublicacionCreada: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPerfil: () -> Unit = {},
    sessionViewModel: SessionViewModel,
    idPublicacionEditar: String? = null,
    viewModel: PublicarViewModel = hiltViewModel()
) {
    val tipoPublicacion by viewModel.tipoPublicacion.collectAsState()
    val titulo          by viewModel.titulo.collectAsState()
    val nombreMascota   by viewModel.nombreMascota.collectAsState()
    val raza            by viewModel.raza.collectAsState()
    val descripcion     by viewModel.descripcion.collectAsState()
    val edad            by viewModel.edad.collectAsState()
    val tipoMascota     by viewModel.tipoMascota.collectAsState()
    val genero          by viewModel.genero.collectAsState()
    val tamaño          by viewModel.tamaño.collectAsState()
    val vacunasAlDia    by viewModel.vacunasAlDia.collectAsState()
    val publicarResult  by viewModel.publicarResult.collectAsState()
    val sessionState    by sessionViewModel.sessionState.collectAsState()
    val idEditando   by viewModel.idPublicacionEditando.collectAsState()
    val motivoRechazo by viewModel.motivoRechazo.collectAsState()
    val modoEdicion = idEditando != null

    var showSuccessDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = publicarResult is RequestResult.Loading

    LaunchedEffect(publicarResult) {
        when (publicarResult) {
            is RequestResult.Success -> {
                showSuccessDialog = true
                viewModel.resetResult()
            }
            is RequestResult.Error -> {
                snackbarHostState.showSnackbar((publicarResult as RequestResult.Error).message)
                viewModel.resetResult()
            }
            else -> Unit
        }
    }
    LaunchedEffect(idPublicacionEditar) {
        idPublicacionEditar?.let { viewModel.cargarParaEdicion(it) }
    }

    // ── Diálogo de éxito ──────────────────────────────────────────────────────
    if (showSuccessDialog) {
        Dialog(onDismissRequest = { showSuccessDialog = false }) {
            Card(
                shape  = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.publicar_success_dialog_title),
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp
                        )
                        IconButton(
                            onClick  = { showSuccessDialog = false; onPublicacionCreada() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.publicar_content_desc_close), tint = PawDarkText)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(36.dp))
                            .background(PawBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint     = PawBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        if(modoEdicion) stringResource(R.string.publicar_success_updated) else stringResource(R.string.publicar_success_sent),
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                        color      = PawDarkText,
                        textAlign  = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        stringResource(R.string.publicar_success_message),
                        fontSize   = 13.sp,
                        color      = PawGrayText,
                        lineHeight = 18.sp,
                        textAlign  = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick  = { showSuccessDialog = false; onPublicacionCreada() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = PawBlue)
                    ) {
                        Text(stringResource(R.string.publicar_button_home), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick  = { showSuccessDialog = false; onNavigateToPerfil() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(
                            contentColor   = PawDarkText,
                            containerColor = AdminBgPage
                        ),
                        border = BorderStroke(1.dp, AdminBgPage)
                    ) {
                        Text(stringResource(R.string.publicar_button_my_posts), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (modoEdicion) stringResource(R.string.publicar_title_edit) else stringResource(R.string.publicar_title_new),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.publicar_content_desc_back), tint = PawDarkText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost    = { SnackbarHost(snackbarHostState) },
        containerColor  = AdminBgPage
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Chips tipo publicación ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                listOf(
                    TipoPublicacion.ADOPCION   to stringResource(R.string.publicar_type_adopcion),
                    TipoPublicacion.PERDIDO    to stringResource(R.string.publicar_type_perdido),
                    TipoPublicacion.ENCONTRADO to stringResource(R.string.publicar_type_encontrado)
                ).forEach { (tipo, label) ->
                    val sel = tipoPublicacion == tipo
                    FilterChip(
                        selected = sel,
                        onClick  = { viewModel.onTipoPublicacionChange(tipo) },
                        label    = { Text(label, fontSize = 13.sp) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PawBlue,
                            selectedLabelColor     = Color.White,
                            containerColor         = AdminBgPage,
                            labelColor             = PawBlue
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled             = true,
                            selected            = sel,
                            borderColor         = PawBlue.copy(alpha = 0.3f),
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            motivoRechazo?.let { motivo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PawGrayWhite)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("⚠️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(stringResource(R.string.publicar_rejected_title), modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,fontWeight = FontWeight.Bold,
                            fontSize = 13.sp, color = PawAmber
                        )
                        Text(motivo, fontSize = 12.sp, color = PawAmber)
                    }
                }
            }

            // ── Foto por defecto (informativo) ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.5.dp, AdminBgPage, RoundedCornerShape(16.dp))
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AdminBgPage),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint     = PawBlue,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(stringResource(R.string.publicar_auto_photo_title), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PawDarkText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        stringResource(R.string.publicar_auto_photo_desc),
                        fontSize  = 12.sp,
                        color     = PawGrayText,
                        lineHeight = 17.sp,
                        modifier  = Modifier.padding(horizontal = 28.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Información básica ─────────────────────────────────────────
            SectionCard {
                SectionTitle(stringResource(R.string.publicar_section_basic_info))
                Spacer(modifier = Modifier.height(16.dp))

                PlainField(
                    label         = stringResource(R.string.publicar_label_title),
                    placeholder   = stringResource(R.string.publicar_placeholder_title),
                    value         = titulo.value,
                    error         = titulo.error,
                    onValueChange = { viewModel.onTituloChange(it) },
                    enabled       = !isLoading
                )

                Spacer(modifier = Modifier.height(10.dp))

                PlainField(
                    label         = stringResource(R.string.publicar_label_pet_name),
                    placeholder   = stringResource(R.string.publicar_placeholder_pet_name),
                    value         = nombreMascota.value,
                    error         = nombreMascota.error,
                    onValueChange = { viewModel.onNombreMascotaChange(it) },
                    enabled       = !isLoading
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Categoría y Tamaño
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        DropdownField(
                            label    = stringResource(R.string.publicar_label_category),
                            selected = tipoMascota.name.lowercase().replaceFirstChar { it.uppercase() },
                            options  = TipoMascota.entries.map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
                            onSelect = { opt ->
                                TipoMascota.entries
                                    .firstOrNull { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } == opt }
                                    ?.let { viewModel.onTipoMascotaChange(it) }
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DropdownField(
                            label    = stringResource(R.string.publicar_label_size),
                            selected = tamaño,
                            options  = listOf(
                                stringResource(R.string.publicar_size_small),
                                stringResource(R.string.publicar_size_medium),
                                stringResource(R.string.publicar_size_large)
                            ),
                            onSelect = { viewModel.onTamañoChange(it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Raza, Edad y Género
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        PlainField(
                            label         = stringResource(R.string.publicar_label_breed),
                            placeholder   = stringResource(R.string.publicar_placeholder_breed),
                            value         = raza.value,
                            error         = raza.error,
                            onValueChange = { viewModel.onRazaChange(it) },
                            enabled       = !isLoading
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PlainField(
                            label         = stringResource(R.string.publicar_label_age),
                            placeholder   = stringResource(R.string.publicar_placeholder_age),
                            value         = edad.value,
                            error         = edad.error,
                            onValueChange = { viewModel.onEdadChange(it) },
                            enabled       = !isLoading,
                            isNumeric     = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                DropdownField(
                    label    = stringResource(R.string.publicar_label_gender),
                    selected = genero,
                    options  = listOf(
                        stringResource(R.string.publicar_gender_male),
                        stringResource(R.string.publicar_gender_female)
                    ),
                    onSelect = { viewModel.onGeneroChange(it) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Descripción
                Column {
                    Text(stringResource(R.string.publicar_label_description), fontSize = 13.sp, color = PawGrayText, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value          = descripcion.value,
                        onValueChange  = { viewModel.onDescripcionChange(it) },
                        placeholder    = { Text(stringResource(R.string.publicar_placeholder_description), color = PawGrayText, fontSize = 13.sp) },
                        modifier       = Modifier.fillMaxWidth().height(110.dp),
                        shape          = RoundedCornerShape(10.dp),
                        isError        = descripcion.error != null,
                        supportingText = { descripcion.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        enabled        = !isLoading,
                        colors         = outlinedFieldColors()
                    )
                }

                HorizontalDivider(color = AdminBgPage, modifier = Modifier.padding(top = 8.dp))

                // Vacunas
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, null, tint = PawBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.publicar_label_vaccines), fontSize = 14.sp, color = PawDarkText)
                    }
                    Switch(
                        checked         = vacunasAlDia,
                        onCheckedChange = { viewModel.onVacunasChange(it) },
                        colors          = SwitchDefaults.colors(
                            checkedThumbColor   = Color.White,
                            checkedTrackColor   = PawBlue,
                            uncheckedThumbColor = PawGrayText,
                            uncheckedTrackColor = PawDarkText
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Ubicación ──────────────────────────────────────────────────
            SectionCard {
                SectionTitle(stringResource(R.string.publicar_section_location))
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AdminBgPage),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗺️", fontSize = 28.sp)
                        Text(stringResource(R.string.publicar_location_default), color = PawBlue, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            stringResource(R.string.publicar_button_change_location),
                            fontSize = 12.sp,
                            color    = PawBlue.copy(alpha = 0.7f),
                            modifier = Modifier.clickable { /* TODO */ }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Botón publicar ─────────────────────────────────────────────
            val idUsuario = (sessionState as? SessionState.Authenticated)?.session?.userId ?: ""
            Button(
                onClick  = {
                    if (modoEdicion) viewModel.guardarCambios()
                    else viewModel.publicar(idUsuario) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape   = RoundedCornerShape(14.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = PawLinkBlue),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color       = Color.White,
                        modifier    = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (modoEdicion) stringResource(R.string.publicar_button_save) else stringResource(R.string.publicar_button_publish), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer { rotationZ = 180f }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Helpers de UI ──────────────────────────────────────────────────────────────

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PawDarkText)
}

@Composable
private fun PlainField(
    label: String,
    placeholder: String,
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    isNumeric: Boolean = false
) {
    Column {
        Text(label, fontSize = 13.sp, color = PawGrayText, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value          = value,
            onValueChange  = onValueChange,
            placeholder    = { Text(placeholder, color = PawGrayText, fontSize = 13.sp) },
            modifier       = Modifier.fillMaxWidth(),
            shape          = RoundedCornerShape(12.dp),
            singleLine     = true,
            isError        = error != null,
            supportingText = { error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            enabled        = enabled,
            keyboardOptions = if (isNumeric)
                androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            else
                androidx.compose.foundation.text.KeyboardOptions.Default,
            colors = outlinedFieldColors()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, fontSize = 13.sp, color = PawGrayText, modifier = Modifier.padding(bottom = 4.dp))
        ExposedDropdownMenuBox(
            expanded         = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value         = selected.ifBlank { stringResource(R.string.publicar_dropdown_default) },
                onValueChange = {},
                readOnly      = true,
                modifier      = Modifier.menuAnchor().fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                trailingIcon  = {
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = PawGrayText)
                },
                colors = outlinedFieldColors()
            )
            ExposedDropdownMenu(
                expanded         = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text    = { Text(opt, fontSize = 14.sp) },
                        onClick = { onSelect(opt); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Color(0xFFB0BEC5),
    unfocusedBorderColor = Color(0xFFCFD8DC),
    focusedContainerColor   = Color(0xFFF7F9FC),
    focusedLabelColor    = Color(0xFFF7F9FC),
    cursorColor          = PawBlue
)
