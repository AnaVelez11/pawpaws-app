package app.pawpaws.domain.model.models

import app.pawpaws.domain.model.enums.AccionAdmin

data class HistorialAccion(
    val id: String,
    val accion: AccionAdmin,
    val idModerador: String,
    val idUsuarioAfectado: String,
    val descripcion: String,
    val fecha: String,
)
