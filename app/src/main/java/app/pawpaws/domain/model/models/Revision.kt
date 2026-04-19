package app.pawpaws.domain.model.models

data class Revision(
    val id: String,
    val decision: String,
    val comentario: String,
    val fecha: String,
    val idModerador: String,
    val idPublicacion: String

)
