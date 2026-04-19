package app.pawpaws.domain.model.models

import app.pawpaws.domain.model.enums.EstadoPublicacion
import app.pawpaws.domain.model.enums.TipoPublicacion

data class Publicacion(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val tipoPublicacion: TipoPublicacion,
    val fechaCreacion: String,
    val fechaActualizacion: String? = null,
    val estado: EstadoPublicacion = EstadoPublicacion.ACTIVA,
    val idMascota: String,
    val idUbicacion: String,
    val idUsuario: String
)
