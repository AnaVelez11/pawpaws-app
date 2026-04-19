package app.pawpaws.domain.model.models

data class Ubicacion(
    val id: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val ciudad: String
)
