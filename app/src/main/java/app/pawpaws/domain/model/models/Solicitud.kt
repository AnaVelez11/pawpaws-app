package app.pawpaws.domain.model.models

import app.pawpaws.domain.model.enums.EstadoSolicitud

data class Solicitud(
    val id: String,
    val mensaje: String,
    val estado: EstadoSolicitud = EstadoSolicitud.PENDIENTE,
    val fecha: String,
    val idPublicacion: String,
    val idSolicitante: String,
    val idPropietario: String
)
