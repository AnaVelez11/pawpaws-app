package app.pawpaws.features.admin.reporte

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import app.pawpaws.core.utils.resources.ImageResources
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisarCasoScreen(
    reporteId: String,
    onNavigateBack: () -> Unit,
    viewModel: ReportesViewModel = hiltViewModel()
) {
    val reportes  by viewModel.reportes.collectAsState()
    val reporte   = reportes.firstOrNull { it.id == reporteId }
    val pub       = reporte?.let { viewModel.findPublicacion(it.idPublicacion) }
    val autor     = pub?.let { viewModel.findUsuario(it.idUsuario) }
    val denunciante = reporte?.let { viewModel.findUsuario(it.idUsuarioReporta) }
    val imagenUrl = reporte?.let { viewModel.resolverImagenPublicacion(it.idPublicacion) } ?: ""
    val historial = viewModel.historialSimulado()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.revisar_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.revisar_cd_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = AdminBgPage
    ) { padding ->

        if (reporte == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.revisar_report_not_found), color = PawGrayText)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── 1. CONTENIDO REPORTADO ─────────────────────────────────────
            SectionCard {
                // Imagen destacada
                AsyncImage(
                    model              = imagenUrl,
                    contentDescription = pub?.titulo,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Título + badge ID
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text       = pub?.titulo ?: stringResource(R.string.revisar_publication_no_title),
                        fontWeight = FontWeight.Bold,
                        fontSize   = 17.sp,
                        color      = PawDarkText,
                        modifier   = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PawBlue.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "#${reporte.idPublicacion.take(6)}",
                            fontSize   = 11.sp,
                            color      = PawBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Autor
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model              = autor?.fotoPerfil ?: ImageResources.DEFAULT_USER,
                        contentDescription = autor?.nombre,
                        modifier           = Modifier.size(32.dp).clip(CircleShape),
                        contentScale       = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            autor?.nombre ?: stringResource(R.string.revisar_user_fallback),
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 13.sp,
                            color      = PawDarkText
                        )
                        Text(
                            autor?.email ?: "",
                            fontSize = 11.sp,
                            color    = PawGrayText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(color = Divider)
                Spacer(modifier = Modifier.height(10.dp))

                // Descripción
                Text(stringResource(R.string.revisar_section_description), fontSize = 12.sp, color = PawGrayText, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    pub?.descripcion ?: "",
                    fontSize   = 13.sp,
                    color      = PawDarkText,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick  = { /* TODO: navegar a detalle publicación */ },
                    shape    = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = PawBlue),
                    border   = androidx.compose.foundation.BorderStroke(1.5.dp, PawBlue)
                ) {
                    Icon(Icons.Default.OpenInNew, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.revisar_btn_view_original))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── 2. DETALLES DEL REPORTE ────────────────────────────────────
            SectionCard {
                SectionLabel(stringResource(R.string.revisar_section_report_details))
                Spacer(modifier = Modifier.height(10.dp))

                // Razón principal — estilo alerta
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PawRed.copy(alpha = 0.07f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, null, tint = PawRed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(stringResource(R.string.revisar_main_reason), fontSize = 11.sp, color = PawGrayText)
                        Text(reporte.motivo, fontSize = 14.sp, color = PawRed, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(stringResource(R.string.revisar_report_description), fontSize = 12.sp, color = PawGrayText)
                Spacer(modifier = Modifier.height(4.dp))
                Text(reporte.descripcion, fontSize = 13.sp, color = PawDarkText, lineHeight = 19.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(stringResource(R.string.revisar_date_label, reporte.fecha), fontSize = 12.sp, color = PawGrayText)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── 3. COMENTARIOS DEL DENUNCIANTE ─────────────────────────────
            SectionCard {
                SectionLabel(stringResource(R.string.revisar_section_comments))
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.Top) {
                    AsyncImage(
                        model              = denunciante?.fotoPerfil ?: ImageResources.DEFAULT_USER,
                        contentDescription = denunciante?.nombre,
                        modifier           = Modifier.size(36.dp).clip(CircleShape),
                        contentScale       = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                denunciante?.nombre ?: stringResource(R.string.revisar_user_fallback),
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 13.sp,
                                color      = PawDarkText
                            )
                            Text(stringResource(R.string.revisar_time_ago), fontSize = 11.sp, color = PawGrayText)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
                                .background(AdminBgPage)
                                .padding(12.dp)
                        ) {
                            Text(
                                reporte.descripcion,
                                fontSize   = 13.sp,
                                color      = PawDarkText,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── 4. HISTORIAL DE REPORTES PREVIOS ──────────────────────────
            SectionCard {
                SectionLabel(stringResource(R.string.revisar_section_history))
                Spacer(modifier = Modifier.height(10.dp))

                historial.forEachIndexed { index, (fecha, motivo, estado) ->
                    val estadoColor = when (estado) {
                        stringResource(R.string.revisar_status_warned)  -> PawAmber
                        stringResource(R.string.revisar_status_discarded) -> PawGrayText
                        stringResource(R.string.revisar_status_resolved)   -> PawGreen
                        else         -> PawGrayText
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(motivo, fontSize = 13.sp, color = PawDarkText)
                            Text(fecha, fontSize = 11.sp, color = PawGrayText)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(estadoColor.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(estado, fontSize = 10.sp, color = estadoColor, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (index < historial.lastIndex) {
                        HorizontalDivider(color = Divider)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── 5. ACCIONES DEL MODERADOR ─────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Acciones secundarias (neutras)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AccionOutlined(
                        label   = stringResource(R.string.revisar_btn_discard),
                        icono   = Icons.Default.Close,
                        color   = PawGrayText,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.descartarReporte(reporteId); onNavigateBack() }
                    )
                    AccionOutlined(
                        label   = stringResource(R.string.revisar_btn_delete_post),
                        icono   = Icons.Default.Delete,
                        color   = PawRed,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.resolverReporte(reporteId); onNavigateBack() }
                    )
                }

                // Acciones principales (advertencia)
                AccionFilled(
                    label   = stringResource(R.string.revisar_btn_send_warning),
                    icono   = Icons.Default.Warning,
                    color   = PawAmber,
                    onClick = { viewModel.resolverReporte(reporteId); onNavigateBack() }
                )

                // Acción peligro (suspensión)
                AccionFilled(
                    label   = stringResource(R.string.revisar_btn_suspend_permanent),
                    icono   = Icons.Default.Block,
                    color   = PawRed,
                    onClick = { viewModel.resolverReporte(reporteId); onNavigateBack() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Composables privados ───────────────────────────────────────────────────────

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
private fun SectionLabel(text: String) {
    Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PawDarkText)
}

@Composable
private fun AccionOutlined(
    label: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick  = onClick,
        modifier = modifier.height(50.dp),
        shape    = RoundedCornerShape(12.dp),
        border   = androidx.compose.foundation.BorderStroke(1.5.dp, color),
        colors   = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        Icon(icono, null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AccionFilled(
    label: String,
    icono: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Icon(icono, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}