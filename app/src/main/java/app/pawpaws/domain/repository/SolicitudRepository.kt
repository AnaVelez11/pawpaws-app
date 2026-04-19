package app.pawpaws.domain.repository

import app.pawpaws.domain.model.enums.EstadoSolicitud
import app.pawpaws.domain.model.models.Solicitud
import kotlinx.coroutines.flow.StateFlow

interface SolicitudRepository {

    val solicitudes: StateFlow<List<Solicitud>>
    fun save(solicitud: Solicitud)
    fun findById(id: String): Solicitud?
    fun findByPublicacion(idPublicacion: String): List<Solicitud>
    fun findBySolicitante(idSolicitante: String): List<Solicitud>
    fun update(solicitud: Solicitud)
    fun actualizarEstado(idSolicitud: String, nuevoEstado: EstadoSolicitud)
}