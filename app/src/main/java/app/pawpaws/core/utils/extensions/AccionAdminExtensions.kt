package app.pawpaws.core.utils.extensions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import app.pawpaws.core.theme.*
import app.pawpaws.domain.model.enums.AccionAdmin

fun AccionAdmin.color(): Color = when (this) {
    AccionAdmin.APROBADA              -> PawGreen
    AccionAdmin.RECHAZADA             -> PawRed
    AccionAdmin.ADVERTENCIA           -> PawAmber
    AccionAdmin.SUSPENSION_TEMPORAL   -> PawRed
    AccionAdmin.SUSPENSION_PERMANENTE -> PawRedDark
    AccionAdmin.ELIMINACION           -> PawRed
}

fun AccionAdmin.icono(): ImageVector = when (this) {
    AccionAdmin.APROBADA              -> Icons.Default.CheckCircle
    AccionAdmin.RECHAZADA             -> Icons.Default.Cancel
    AccionAdmin.ADVERTENCIA           -> Icons.Default.Warning
    AccionAdmin.SUSPENSION_TEMPORAL   -> Icons.Default.Block
    AccionAdmin.SUSPENSION_PERMANENTE -> Icons.Default.DeleteForever
    AccionAdmin.ELIMINACION           -> Icons.Default.Delete
}

fun AccionAdmin.etiqueta(): String = when (this) {
    AccionAdmin.APROBADA              -> "Aprobada"
    AccionAdmin.RECHAZADA             -> "Rechazada"
    AccionAdmin.ADVERTENCIA           -> "Advertencia"
    AccionAdmin.SUSPENSION_TEMPORAL   -> "Suspensión temporal"
    AccionAdmin.SUSPENSION_PERMANENTE -> "Suspensión permanente"
    AccionAdmin.ELIMINACION           -> "Eliminación"
}