package app.pawpaws.features.configuracion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pawpaws.R
import app.pawpaws.core.theme.AdminBgPage
import app.pawpaws.core.theme.PawBlue
import app.pawpaws.core.theme.PawDarkText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    onBack: () -> Unit
) {

    var notifGeneral by remember { mutableStateOf(true) }
    var notifMascotas by remember { mutableStateOf(true) }
    var notifComentarios by remember { mutableStateOf(false) }
    var notifSolicitudes by remember { mutableStateOf(true) }

    var radio by remember { mutableStateOf(15f) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.configuracion_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        containerColor = AdminBgPage
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(stringResource(R.string.configuracion_notifications), fontSize = 14.sp, color = PawDarkText)

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AdminBgPage)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    SwitchItem(stringResource(R.string.configuracion_general), Icons.Default.Notifications, notifGeneral) {
                        notifGeneral = it
                    }
                    SwitchItem(stringResource(R.string.configuracion_nearby_pets), Icons.Default.Pets, notifMascotas) {
                        notifMascotas = it
                    }
                    SwitchItem(stringResource(R.string.configuracion_comments), Icons.Default.ChatBubbleOutline, notifComentarios) {
                        notifComentarios = it
                    }
                    SwitchItem(stringResource(R.string.configuracion_requests), Icons.Default.Handshake, notifSolicitudes) {
                        notifSolicitudes = it
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(stringResource(R.string.configuracion_location), fontSize = 14.sp, color = PawDarkText)

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AdminBgPage)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.configuracion_search_radius))
                        Text(stringResource(R.string.configuracion_km, radio.toInt()), color = PawBlue)
                    }

                    Slider(
                        value = radio,
                        onValueChange = { radio = it },
                        valueRange = 1f..50f
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AdminBgPage)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.configuracion_change_location))
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange
        )
    }
}