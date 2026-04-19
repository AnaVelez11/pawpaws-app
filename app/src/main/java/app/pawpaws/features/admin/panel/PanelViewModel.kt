package app.pawpaws.features.admin.panel

import androidx.lifecycle.ViewModel
import app.pawpaws.domain.model.models.HistorialAccion
import app.pawpaws.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PanelViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    val historial: StateFlow<List<HistorialAccion>> = adminRepository.historial

    val pendientes: Int get() = adminRepository.publicacionesPendientes.value.size
    val reportesPendientes: Int get() = adminRepository.reportes.value
        .count { it.estado == app.pawpaws.domain.model.enums.EstadoReporte.PENDIENTE }
    val aprobadas: Int  get() = adminRepository.totalAprobadas()
    val rechazadas: Int get() = adminRepository.totalRechazadas()
}