package app.pawpaws.domain.model.models

import app.pawpaws.domain.model.enums.EstadoReporte

data class Reporte(
    val id: String,
    val motivo: String,
    val descripcion: String,
    val fecha: String,
    val estado: EstadoReporte = EstadoReporte.PENDIENTE,
    val idPublicacion: String,
    val idUsuarioReporta: String

)
