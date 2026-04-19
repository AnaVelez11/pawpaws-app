package app.pawpaws.domain.model.models

data class PerfilStats(
    val activas: Int,
    val pendientes: Int,
    val completadas: Int,
    val rechazadas: Int
)