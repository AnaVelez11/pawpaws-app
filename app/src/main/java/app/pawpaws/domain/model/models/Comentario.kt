package app.pawpaws.domain.model.models

data class Comentario(
    val id: String,
    val idUsuario: String,
    val idPublicacion: String,
    val texto: String,
    val fecha: String
)
