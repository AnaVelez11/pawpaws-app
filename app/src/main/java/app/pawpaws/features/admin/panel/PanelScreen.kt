package app.pawpaws.features.admin.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.pawpaws.R
import app.pawpaws.core.theme.*
import app.pawpaws.core.utils.extensions.color
import app.pawpaws.core.utils.extensions.etiqueta
import app.pawpaws.core.utils.extensions.icono
import app.pawpaws.domain.model.models.HistorialAccion

@Composable
fun PanelScreen(
    onHistorial: () -> Unit,
    viewModel: PanelViewModel = hiltViewModel()
) {
    val historial by viewModel.historial.collectAsState()
    val pendientes = viewModel.pendientes
    val reportesPendientes = viewModel.reportesPendientes
    val aprobadas = viewModel.aprobadas
    val rechazadas = viewModel.rechazadas

    Scaffold(
        containerColor = AdminBgPage,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AdminBgCard)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(stringResource(R.string.panel_title), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PawDarkText)
                Text(
                    stringResource(R.string.panel_metrics_header),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PawGrayText
                )
                Text(
                    stringResource(R.string.panel_metrics_description),
                    fontSize = 13.sp,
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

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Métricas fila 1
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricaCard(
                            valor = pendientes.toString(),
                            label = stringResource(R.string.panel_metric_pending),
                            icono = Icons.Default.HourglassEmpty,
                            color = PawAmber,
                            modifier = Modifier.weight(1f)
                        )
                        MetricaCard(
                            valor = aprobadas.toString(),
                            label = stringResource(R.string.panel_metric_approved),
                            icono = Icons.Default.CheckCircle,
                            color = PawGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Métricas fila 2
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricaCard(
                            valor = rechazadas.toString(),
                            label = stringResource(R.string.panel_metric_rejected),
                            icono = Icons.Default.Cancel,
                            color = PawRed,
                            modifier = Modifier.weight(1f)
                        )
                        MetricaCard(
                            valor = reportesPendientes.toString(),
                            label = stringResource(R.string.panel_metric_reports),
                            icono = Icons.Default.Flag,
                            color = PawBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }


                // Tiempo promedio
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AdminBgCard),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                null,
                                tint = PawBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.panel_avg_review_time_label),
                                    fontSize = 13.sp,
                                    color = PawGrayText
                                )
                                Text(
                                    stringResource(R.string.panel_avg_review_time_value),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PawDarkText
                                )
                            }
                        }
                    }
                }

                // Gráfico simulado
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AdminBgCard),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                stringResource(R.string.panel_weekly_activity),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = PawDarkText
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            GraficoSimulado()
                        }
                    }
                }

                // Botón historial
                item {
                    Button(
                        onClick = onHistorial,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PawLinkBlue)
                    ) {
                        Icon(Icons.Default.History, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.panel_btn_history), fontWeight = FontWeight.Medium)
                    }
                }

                // Acciones recientes
                item {
                    Text(
                        stringResource(R.string.panel_recent_actions),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PawDarkText
                    )
                }

                items(historial.take(5), key = { it.id }) { accion ->
                    HistorialItem(accion)
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

// ── Composables privados ───────────────────────────────────────────────────────

@Composable
private fun MetricaCard(
    valor: String,
    label: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = AdminBgCard),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icono, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(valor, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PawDarkText)
            Text(label, fontSize = 12.sp, color = PawGrayText)
        }
    }
}

@Composable
private fun GraficoSimulado() {
    val barras = listOf(60, 85, 40, 90, 70, 55, 80)
    val dias   = listOf(
        stringResource(R.string.panel_day_monday),
        stringResource(R.string.panel_day_tuesday),
        stringResource(R.string.panel_day_wednesday),
        stringResource(R.string.panel_day_thursday),
        stringResource(R.string.panel_day_friday),
        stringResource(R.string.panel_day_saturday),
        stringResource(R.string.panel_day_sunday)
    )
    val max    = barras.max().toFloat()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.Bottom
    ) {
        barras.forEachIndexed { i, valor ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier            = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .fillMaxHeight(valor / max)
                        .background(
                            color = if (i == 3) PawBlue else PawBlue.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(dias[i], fontSize = 10.sp, color = PawGrayText)
            }
        }
    }
}

@Composable
private fun HistorialItem(accion: HistorialAccion) {
    val color   = accion.accion.color()
    val icono   = accion.accion.icono()
    val etiqueta = accion.accion.etiqueta()

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = AdminBgCard),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = etiqueta, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(etiqueta, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = PawDarkText)
                Text(accion.descripcion, fontSize = 12.sp, color = PawGrayText)
                Text(accion.fecha, fontSize = 11.sp, color = PawGrayText)
            }
        }
    }
}