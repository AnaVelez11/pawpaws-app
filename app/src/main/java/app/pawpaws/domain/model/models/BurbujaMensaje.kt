package app.pawpaws.domain.model.models

data class BurbujaMensaje(
    val id: String,
    val texto: String,
    val esMio: Boolean,       // true = derecha, false = izquierda
    val hora: String,
    val leido: Boolean = true
)