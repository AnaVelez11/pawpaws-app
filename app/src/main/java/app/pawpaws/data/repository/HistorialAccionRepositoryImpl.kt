package app.pawpaws.data.repository

import app.pawpaws.domain.model.models.HistorialAccion
import app.pawpaws.domain.repository.HistorialAccionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistorialAccionRepositoryImpl @Inject constructor() : HistorialAccionRepository {
    private val _historial = MutableStateFlow<List<HistorialAccion>>(emptyList())
    override val historial: StateFlow<List<HistorialAccion>> = _historial.asStateFlow()

    override fun save(accion: HistorialAccion) { _historial.value += accion }
    override fun findByModerador(idModerador: String): List<HistorialAccion> =
        _historial.value.filter { it.idModerador == idModerador }
}