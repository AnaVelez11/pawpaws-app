package app.pawpaws.domain.repository

import app.pawpaws.domain.model.models.Reporte
import kotlinx.coroutines.flow.StateFlow

interface ReporteRepository {

    val reportes: StateFlow<List<Reporte>>
    fun save(reporte: Reporte)
    fun findByPublicacion(idPublicacion: String): List<Reporte>
    fun update(reporte: Reporte)
}