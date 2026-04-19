package app.pawpaws.domain.repository

import app.pawpaws.domain.model.models.HistorialAccion
import kotlinx.coroutines.flow.StateFlow

interface HistorialAccionRepository {
    val historial: StateFlow<List<HistorialAccion>>
    fun save(accion: HistorialAccion)
    fun findByModerador(idModerador: String): List<HistorialAccion>
}