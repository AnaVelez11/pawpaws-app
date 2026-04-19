package app.pawpaws.domain.model.models

data class Notificacion(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val leida: Boolean = false,
    val idUsuario: String
)
