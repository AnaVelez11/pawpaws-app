package app.pawpaws.data.repository

import app.pawpaws.domain.model.enums.EstadoSolicitud
import app.pawpaws.domain.model.models.Solicitud
import app.pawpaws.domain.repository.SolicitudRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SolicitudRepositoryImpl @Inject constructor() : SolicitudRepository {

    private val _solicitudes = MutableStateFlow<List<Solicitud>>(emptyList())
    override val solicitudes: StateFlow<List<Solicitud>> = _solicitudes.asStateFlow()

    init {
        _solicitudes.value = listOf(
            Solicitud(
                id = "s1", mensaje = "Me encantaría adoptar a Max, tengo jardín grande.",
                estado = EstadoSolicitud.PENDIENTE, fecha = "2025-01-11",
                idPublicacion = "p1", idSolicitante = "2", idPropietario = "1"
            )
        )
    }

    override fun save(solicitud: Solicitud) { _solicitudes.value += solicitud }

    override fun findById(id: String): Solicitud? = _solicitudes.value.firstOrNull { it.id == id }

    override fun findByPublicacion(idPublicacion: String): List<Solicitud> =
        _solicitudes.value.filter { it.idPublicacion == idPublicacion }

    override fun findBySolicitante(idSolicitante: String): List<Solicitud> =
        _solicitudes.value.filter { it.idSolicitante == idSolicitante }

    override fun update(solicitud: Solicitud) {
        _solicitudes.value = _solicitudes.value.map { if (it.id == solicitud.id) solicitud else it }
    }

    override fun actualizarEstado(idSolicitud: String, nuevoEstado: EstadoSolicitud) {
        _solicitudes.value = _solicitudes.value.map { sol ->
            if (sol.id == idSolicitud) sol.copy(estado = nuevoEstado) else sol
        }
    }
}