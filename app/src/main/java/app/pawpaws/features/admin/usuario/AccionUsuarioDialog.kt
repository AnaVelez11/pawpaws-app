package app.pawpaws.features.admin.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.pawpaws.R
import app.pawpaws.core.theme.*

// ── 1. MODAL: ENVIAR ADVERTENCIA ─────────────────────────────────────────────

@Composable
fun AdvertenciaDialog(
    usuarioId: String,
    onDismiss: () -> Unit,
    onConfirmar: (motivo: String, mensaje: String) -> Unit
) {
    val motivosAdvertencia = listOf(
        stringResource(R.string.accion_warning_reason_inappropriate),
        stringResource(R.string.accion_warning_reason_false_info),
        stringResource(R.string.accion_warning_reason_suspicious),
        stringResource(R.string.accion_warning_reason_spam),
        stringResource(R.string.accion_warning_reason_other)
    )
    var motivoSeleccionado by remember { mutableStateOf("") }
    var mensajeAdicional   by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Encabezado
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PawAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Warning, null, tint = PawAmber, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(stringResource(R.string.accion_warning_title), fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PawDarkText)
                        Text(stringResource(R.string.accion_warning_subtitle), fontSize = 13.sp, color = PawGrayText)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Divider)
                Spacer(modifier = Modifier.height(16.dp))

                // Motivos
                Text(stringResource(R.string.accion_warning_reason_title), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PawDarkText)
                Spacer(modifier = Modifier.height(10.dp))

                motivosAdvertencia.forEach { motivo ->
                    val seleccionado = motivoSeleccionado == motivo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (seleccionado) 2.dp else 1.dp,
                                color = if (seleccionado) PawAmber else PawAmber,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(if (seleccionado) PawAmber.copy(alpha = 0.06f) else Color.White)
                            .clickable { motivoSeleccionado = motivo }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = seleccionado,
                            onClick  = { motivoSeleccionado = motivo },
                            colors   = RadioButtonDefaults.colors(selectedColor = PawAmber)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(motivo, fontSize = 14.sp, color = PawDarkText)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Mensaje adicional
                Text(stringResource(R.string.accion_warning_additional_message), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PawDarkText)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value         = mensajeAdicional,
                    onValueChange = { mensajeAdicional = it },
                    placeholder   = { Text(stringResource(R.string.accion_warning_placeholder_message), fontSize = 13.sp) },
                    modifier      = Modifier.fillMaxWidth().height(90.dp),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = PawAmber,
                        unfocusedBorderColor = BgPage
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Botones
                Button(
                    onClick  = {
                        if (motivoSeleccionado.isNotBlank()) {
                            onConfirmar(motivoSeleccionado, mensajeAdicional)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = PawAmber),
                    enabled  = motivoSeleccionado.isNotBlank()
                ) {
                    Icon(Icons.Default.Warning, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.accion_warning_send), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick  = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.accion_cancel), color = PawGrayText, fontSize = 14.sp)
                }
            }
        }
    }
}

// ── 2. MODAL: SUSPENSIÓN TEMPORAL ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuspensionTemporalDialog(
    usuarioId: String,
    onDismiss: () -> Unit,
    onConfirmar: (duracion: String, motivo: String, detalles: String) -> Unit
) {
    val duraciones = listOf(
        stringResource(R.string.accion_temporal_duration_24h),
        stringResource(R.string.accion_temporal_duration_3d),
        stringResource(R.string.accion_temporal_duration_7d)
    )
    val motivosSuspension = listOf(
        stringResource(R.string.accion_temporal_reason_recidivism),
        stringResource(R.string.accion_temporal_reason_confirmed_reports),
        stringResource(R.string.accion_temporal_reason_inappropriate),
        stringResource(R.string.accion_temporal_reason_misuse)
    )
    var duracionSeleccionada by remember { mutableStateOf("24 horas") }
    var motivoSeleccionado   by remember { mutableStateOf("") }
    var detallesAdicionales  by remember { mutableStateOf("") }
    var expandedDropdown     by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Encabezado
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PawOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Block, null, tint = PawOrange, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(stringResource(R.string.accion_temporal_title), fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PawDarkText)
                        Text(stringResource(R.string.accion_temporal_subtitle), fontSize = 13.sp, color = PawGrayText)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Divider)
                Spacer(modifier = Modifier.height(16.dp))

                // Duración — dropdown
                Text(stringResource(R.string.accion_temporal_duration_title), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PawDarkText)
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded         = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value         = duracionSeleccionada,
                        onValueChange = {},
                        readOnly      = true,
                        modifier      = Modifier.menuAnchor().fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        trailingIcon  = {
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = PawGrayText)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = PawOrange,
                            unfocusedBorderColor = BgPage
                        )
                    )
                    ExposedDropdownMenu(
                        expanded         = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        duraciones.forEach { dur ->
                            DropdownMenuItem(
                                text    = { Text(dur) },
                                onClick = { duracionSeleccionada = dur; expandedDropdown = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Motivos
                Text(stringResource(R.string.accion_temporal_reason_title), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PawDarkText)
                Spacer(modifier = Modifier.height(10.dp))

                motivosSuspension.forEach { motivo ->
                    val seleccionado = motivoSeleccionado == motivo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (seleccionado) 2.dp else 1.dp,
                                color = if (seleccionado) PawOrange else PawAmber,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(if (seleccionado) PawOrange.copy(alpha = 0.06f) else Color.White)
                            .clickable { motivoSeleccionado = motivo }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = seleccionado,
                            onClick  = { motivoSeleccionado = motivo },
                            colors   = RadioButtonDefaults.colors(selectedColor = PawOrange)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(motivo, fontSize = 14.sp, color = PawDarkText)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Detalles adicionales
                Text(stringResource(R.string.accion_temporal_additional_details), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PawDarkText)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value         = detallesAdicionales,
                    onValueChange = { detallesAdicionales = it },
                    placeholder   = { Text(stringResource(R.string.accion_temporal_placeholder_details), fontSize = 13.sp) },
                    modifier      = Modifier.fillMaxWidth().height(80.dp),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = PawOrange,
                        unfocusedBorderColor = BgPage
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick  = {
                        if (motivoSeleccionado.isNotBlank()) {
                            onConfirmar(duracionSeleccionada, motivoSeleccionado, detallesAdicionales)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = PawOrange),
                    enabled  = motivoSeleccionado.isNotBlank()
                ) {
                    Icon(Icons.Default.Block, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.accion_temporal_suspend), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick  = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.accion_cancel), color = PawGrayText, fontSize = 14.sp)
                }
            }
        }
    }
}

// ── 3. MODAL: SUSPENSIÓN PERMANENTE (CRÍTICO) ─────────────────────────────────

@Composable
fun SuspensionPermanenteDialog(
    usuarioId: String,
    onDismiss: () -> Unit,
    onConfirmar: (motivo: String, justificacion: String) -> Unit
) {
    val motivosPermanente = listOf(
        stringResource(R.string.accion_permanent_reason_animal_abuse),
        stringResource(R.string.accion_permanent_reason_illegal_sale),
        stringResource(R.string.accion_permanent_reason_fraud),
        stringResource(R.string.accion_permanent_reason_impersonation)
    )
    var motivoSeleccionado by remember { mutableStateOf("") }
    var justificacion      by remember { mutableStateOf("") }
    var confirmado         by remember { mutableStateOf(false) }

    val puedeConfirmar = motivoSeleccionado.isNotBlank() && justificacion.isNotBlank() && confirmado

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Encabezado
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PawRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DeleteForever, null, tint = PawRed, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(stringResource(R.string.accion_permanent_title), fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PawDarkText)
                        Text(stringResource(R.string.accion_permanent_subtitle), fontSize = 13.sp, color = PawGrayText)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Divider)
                Spacer(modifier = Modifier.height(16.dp))

                // 2. Motivos
                Text(stringResource(R.string.accion_permanent_reason_title), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PawDarkText)
                Spacer(modifier = Modifier.height(10.dp))

                motivosPermanente.forEach { motivo ->
                    val seleccionado = motivoSeleccionado == motivo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (seleccionado) 2.dp else 1.dp,
                                color = if (seleccionado) PawRed else PawAmber,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(if (seleccionado) PawRed.copy(alpha = 0.06f) else Color.White)
                            .clickable { motivoSeleccionado = motivo }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
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

                Spacer(modifier = Modifier.height(4.dp))

                // 3. Justificación
                Text(stringResource(R.string.accion_permanent_justification_title), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PawDarkText)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value         = justificacion,
                    onValueChange = { justificacion = it },
                    placeholder   = { Text(stringResource(R.string.accion_permanent_placeholder_justification), fontSize = 13.sp) },
                    modifier      = Modifier.fillMaxWidth().height(90.dp),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = PawRed,
                        unfocusedBorderColor = BgPage
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { confirmado = !confirmado },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = confirmado,
                        onCheckedChange = { confirmado = it },
                        colors = CheckboxDefaults.colors(checkedColor = PawRed)
                    )
                    Text(
                        stringResource(R.string.accion_permanent_confirmation),
                        fontSize = 13.sp,
                        color = PawDarkText
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 5. Botón de Acción
                Button(
                    onClick  = { if (puedeConfirmar) onConfirmar(motivoSeleccionado, justificacion) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = PawRed,
                        disabledContainerColor = PawRed.copy(alpha = 0.4f)
                    ),
                    enabled  = puedeConfirmar
                ) {
                    Icon(Icons.Default.DeleteForever, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.accion_permanent_suspend), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick  = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.accion_cancel), color = PawGrayText, fontSize = 14.sp)
                }
            }
        }
    }
}