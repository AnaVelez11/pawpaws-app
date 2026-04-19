package app.pawpaws.data.repository

import app.pawpaws.domain.model.enums.EstadoReporte
import app.pawpaws.domain.model.models.Reporte
import app.pawpaws.domain.repository.ReporteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReporteRepositoryImpl @Inject constructor() : ReporteRepository {
    private val _reportes = MutableStateFlow<List<Reporte>>(emptyList())
    override val reportes: StateFlow<List<Reporte>> = _reportes.asStateFlow()

    init {
        _reportes.value = listOf(
            Reporte("r1", "Contenido inapropiado", "La publicación contiene imágenes falsas.",
                "2025-01-14", EstadoReporte.PENDIENTE, "p2", "2")
        )
    }

    override fun save(reporte: Reporte) { _reportes.value += reporte }
    override fun findByPublicacion(idPublicacion: String): List<Reporte> =
        _reportes.value.filter { it.idPublicacion == idPublicacion }
    override fun update(reporte: Reporte) {
        _reportes.value = _reportes.value.map { if (it.id == reporte.id) reporte else it }
    }
}
