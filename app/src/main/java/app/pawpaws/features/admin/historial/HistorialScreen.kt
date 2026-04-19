package app.pawpaws.features.admin.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.pawpaws.core.theme.*
import app.pawpaws.core.utils.extensions.color
import app.pawpaws.core.utils.extensions.etiqueta
import app.pawpaws.core.utils.extensions.icono
import app.pawpaws.domain.model.models.HistorialAccion
import app.pawpaws.features.admin.panel.PanelViewModel
import app.pawpaws.R



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onNavigateBack: () -> Unit,
    viewModel: PanelViewModel = hiltViewModel()
) {
    val historial by viewModel.historial.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text( text = stringResource(R.string.historial_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_volver))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminBgCard)
            )
        },
        containerColor = AdminBgPage
    ) { padding ->

        if (historial.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.historial_empty), color = PawGrayText)
            }
        } else {
            LazyColumn(
                modifier        = Modifier.fillMaxSize().padding(padding),
                contentPadding  = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(historial, key = { it.id }) { accion ->
                    HistorialDetalleItem(accion)
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun HistorialDetalleItem(accion: HistorialAccion) {
    val color    = accion.accion.color()
    val icono    = accion.accion.icono()
    val etiqueta = accion.accion.etiqueta()

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = AdminBgCard),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icono,
                    contentDescription = etiqueta,
                    tint               = color,
                    modifier           = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(etiqueta, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = PawDarkText)
                Text(accion.descripcion, fontSize = 12.sp, color = PawGrayText)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.historial_label_moderador, accion.idModerador),
                        fontSize = 11.sp, color = PawGrayText)
                    Text("·", fontSize = 11.sp, color = PawGrayText)
                    Text(accion.fecha, fontSize = 11.sp, color = PawGrayText)
                }
            }
        }
    }
}