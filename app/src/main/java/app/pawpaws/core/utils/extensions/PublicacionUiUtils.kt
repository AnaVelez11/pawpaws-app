package app.pawpaws.core.utils.extensions

import androidx.compose.ui.graphics.Color
import app.pawpaws.domain.model.enums.TipoPublicacion

fun tipoBadgeColor(tipo: TipoPublicacion): Color {
    return when (tipo) {
        TipoPublicacion.ADOPCION -> Color(0xFF4CAF50)
        TipoPublicacion.PERDIDO -> Color(0xFFF44336)
        TipoPublicacion.ENCONTRADO -> Color(0xFF2196F3)
    }
}

fun tipoLabel(tipo: TipoPublicacion): String {
    return when (tipo) {
        TipoPublicacion.ADOPCION -> "Adopción"
        TipoPublicacion.PERDIDO -> "Perdido"
        TipoPublicacion.ENCONTRADO -> "Encontrado"
    }
}