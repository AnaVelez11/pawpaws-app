package app.pawpaws.features.admin.reporte

import androidx.lifecycle.ViewModel
import app.pawpaws.R
import app.pawpaws.domain.mapper.ImageMapper
import app.pawpaws.domain.model.enums.TipoMascota
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.model.models.Reporte
import app.pawpaws.domain.model.models.Usuario
import app.pawpaws.domain.repository.AdminRepository
import app.pawpaws.domain.repository.MascotaRepository
import app.pawpaws.domain.repository.PublicacionRepository
import app.pawpaws.domain.repository.ResourceProvider
import app.pawpaws.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ReportesViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val publicacionRepository: PublicacionRepository,
    private val mascotaRepository: MascotaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    val reportes: StateFlow<List<Reporte>> = adminRepository.reportes

    fun resolverReporte(id: String)  { adminRepository.resolverReporte(id) }
    fun descartarReporte(id: String) { adminRepository.descartarReporte(id) }

    fun findPublicacion(id: String): Publicacion? =
        publicacionRepository.findById(id)
            ?: adminRepository.publicacionesPendientes.value.firstOrNull { it.id == id }

    fun findUsuario(id: String): Usuario? = usuarioRepository.findById(id)

    fun resolverImagenPublicacion(idPublicacion: String): String {
        val pub = findPublicacion(idPublicacion) ?: return ""
        return resolverImagen(pub)
    }

    fun resolverImagen(publicacion: Publicacion): String {
        val fotos   = publicacionRepository.fotosByPublicacion(publicacion.id)
        val mascota = mascotaRepository.findById(publicacion.idMascota)
        return fotos.firstOrNull()?.url
            ?: mascota?.let { ImageMapper.resolverImagen(publicacion.tipoPublicacion, it.tipo) }
            ?: ImageMapper.resolverImagen(publicacion.tipoPublicacion, TipoMascota.PERRO)
    }

    /** Reportes previos simulados para el historial en RevisarCasoScreen */
    fun historialSimulado(): List<Triple<String, String, String>> = listOf(
        Triple(resourceProvider.getString(R.string.reportes_historial_date1), resourceProvider.getString(R.string.reportes_historial_warning_description), resourceProvider.getString(R.string.reportes_historial_warning_status)),
        Triple(resourceProvider.getString(R.string.reportes_historial_date2), resourceProvider.getString(R.string.reportes_historial_discarded_description), resourceProvider.getString(R.string.reportes_historial_discarded_status))
    )
}