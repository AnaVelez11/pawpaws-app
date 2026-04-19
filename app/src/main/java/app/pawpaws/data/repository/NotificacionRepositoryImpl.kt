package app.pawpaws.data.repository

import app.pawpaws.domain.model.models.Notificacion
import app.pawpaws.domain.repository.NotificacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificacionRepositoryImpl @Inject constructor() : NotificacionRepository {

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    override val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    init {
        _notificaciones.value = listOf(
            Notificacion("n1", "Nueva solicitud", "Ana quiere adoptar a Max.", "2025-01-11", false, "1"),
            Notificacion("n2", "Mensaje nuevo", "Tienes un mensaje de Juan.", "2025-01-12", false, "2"),
            Notificacion("n3", "Solicitud aceptada", "Tu solicitud fue aceptada.", "2025-01-13", true, "2")
        )
    }

    override fun save(notificacion: Notificacion) { _notificaciones.value += notificacion }

    override fun findByUsuario(idUsuario: String): List<Notificacion> =
        _notificaciones.value.filter { it.idUsuario == idUsuario }

    override fun marcarLeida(id: String) {
        _notificaciones.value = _notificaciones.value.map {
            if (it.id == id) it.copy(leida = true) else it
        }
    }
}