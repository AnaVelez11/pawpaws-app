package app.pawpaws.features.admin.reporte

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.Indicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import app.pawpaws.core.theme.*
import app.pawpaws.domain.model.enums.EstadoReporte
import app.pawpaws.domain.model.models.Reporte
import coil.compose.AsyncImage

@Composable
fun ReportesScreen(
    onRevisarCaso: (String) -> Unit,
    viewModel: ReportesViewModel = hiltViewModel()
) {
    val reportes by viewModel.reportes.collectAsState()
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs     = listOf(
        stringResource(R.string.reporte_tab_publications),
        stringResource(R.string.reporte_tab_users),
        stringResource(R.string.reporte_tab_comments)
    )

    val filtrados = when (tabIndex) {
        0    -> reportes.filter { it.idPublicacion.isNotBlank() }
        else -> emptyList()
    }

    Scaffold(
        containerColor = AdminBgPage,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(stringResource(R.string.reporte_title), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PawDarkText)
                Text(
                    stringResource(R.string.reporte_pending_count, reportes.count { it.estado == EstadoReporte.PENDIENTE }),
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = PawGrayText
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor   = Color.White,
                contentColor     = PawBlue,
                indicator = { positions ->
                    Indicator(
                        modifier = Modifier.tabIndicatorOffset(positions[tabIndex]),
                        color    = PawBlue,
                        height   = 2.dp
                    )
                }
            ) {
                tabs.forEachIndexed { i, label ->
                    Tab(
                        selected = tabIndex == i,
                        onClick  = { tabIndex = i },
                        text     = {
                            Text(
                                label,
                                fontSize   = 13.sp,
                                fontWeight = if (tabIndex == i) FontWeight.Bold else FontWeight.Normal,
                                color      = if (tabIndex == i) PawBlue else PawGrayText
                            )
                        }
                    )
                }
            }

            when {
                tabIndex != 0 -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.reporte_coming_soon), color = PawGrayText, fontSize = 15.sp)
                }
                filtrados.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.reporte_empty), color = PawGrayText, fontSize = 15.sp)
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtrados, key = { it.id }) { reporte ->
                        val pub       = viewModel.findPublicacion(reporte.idPublicacion)
                        val autor     = pub?.let { viewModel.findUsuario(it.idUsuario) }
                        val imagenUrl = viewModel.resolverImagenPublicacion(reporte.idPublicacion)

                        ReporteCard(
                            reporte   = reporte,
                            imagenUrl = imagenUrl,
                            titulo    = pub?.titulo ?: stringResource(R.string.reporte_publication_fallback),
                            nombreAutor = autor?.nombre ?: stringResource(R.string.reporte_user_fallback),
                            fotoAutor   = autor?.fotoPerfil,
                            onRevisar = { onRevisarCaso(reporte.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ReporteCard(
    reporte: Reporte,
    imagenUrl: String,
    titulo: String,
    nombreAutor: String,
    fotoAutor: String?,
    onRevisar: () -> Unit
) {
    val estadoColor = when (reporte.estado) {
        EstadoReporte.PENDIENTE  -> PawAmber
        EstadoReporte.RESUELTO   -> PawGreen
        EstadoReporte.DESCARTADO -> PawGrayText
    }
    val estadoLabel = when (reporte.estado) {
        EstadoReporte.PENDIENTE  -> stringResource(R.string.reporte_status_pending)
        EstadoReporte.RESUELTO   -> stringResource(R.string.reporte_status_resolved)
        EstadoReporte.DESCARTADO -> stringResource(R.string.reporte_status_discarded)
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            // Imagen destacada
            AsyncImage(
                model              = imagenUrl,
                contentDescription = titulo,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(14.dp)) {
                // Título + motivo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = PawDarkText,
                        modifier   = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(estadoColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(estadoLabel, color = estadoColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Motivo — estilo alerta
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PawRed.copy(alpha = 0.07f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(stringResource(R.string.reporte_icon_warning), fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(reporte.motivo, fontSize = 12.sp, color = PawRed, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Autor
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (fotoAutor != null) {
                        AsyncImage(
                            model              = fotoAutor,
                            contentDescription = nombreAutor,
                            modifier           = Modifier.size(22.dp).clip(CircleShape),
                            contentScale       = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(PawBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(nombreAutor.first().uppercase(), fontSize = 10.sp, color = PawBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(nombreAutor, fontSize = 12.sp, color = PawGrayText)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("·", fontSize = 12.sp, color = PawGrayText)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(reporte.fecha, fontSize = 11.sp, color = PawGrayText)
                }

                if (reporte.estado == EstadoReporte.PENDIENTE) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Divider)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick        = onRevisar,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                stringResource(R.string.reporte_btn_review),
                                color      = PawBlue,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}