package app.pawpaws.domain.repository

import app.pawpaws.domain.model.models.Notificacion
import kotlinx.coroutines.flow.StateFlow

interface NotificacionRepository {

    val notificaciones: StateFlow<List<Notificacion>>
    fun save(notificacion: Notificacion)
    fun findByUsuario(idUsuario: String): List<Notificacion>
    fun marcarLeida(id: String)
}