package app.pawpaws.features.admin.revision

import androidx.lifecycle.ViewModel
import app.pawpaws.domain.model.models.Mascota
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.model.models.Ubicacion
import app.pawpaws.domain.model.models.Usuario
import app.pawpaws.domain.repository.AdminRepository
import app.pawpaws.domain.repository.MascotaRepository
import app.pawpaws.domain.repository.PublicacionRepository
import app.pawpaws.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RevisionViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val publicacionRepository: PublicacionRepository,
    private val mascotaRepository: MascotaRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _publicacion = MutableStateFlow<Publicacion?>(null)
    val publicacion: StateFlow<Publicacion?> = _publicacion.asStateFlow()

    private val _mascota = MutableStateFlow<Mascota?>(null)
    val mascota: StateFlow<Mascota?> = _mascota.asStateFlow()

    private val _propietario = MutableStateFlow<Usuario?>(null)
    val propietario: StateFlow<Usuario?> = _propietario.asStateFlow()

    private val _ubicacion = MutableStateFlow<Ubicacion?>(null)
    val ubicacion: StateFlow<Ubicacion?> = _ubicacion.asStateFlow()

    private val _imagenUrl = MutableStateFlow("")
    val imagenUrl: StateFlow<String> = _imagenUrl.asStateFlow()

    private val _accionRealizada = MutableStateFlow<AccionRevision>(AccionRevision.Ninguna)
    val accionRealizada: StateFlow<AccionRevision> = _accionRealizada.asStateFlow()

    fun cargar(publicacionId: String) {
        // Buscar primero en pendientes del admin, luego en repo general
        val pub = adminRepository.publicacionesPendientes.value.firstOrNull { it.id == publicacionId }
            ?: publicacionRepository.findById(publicacionId)
            ?: return

        _publicacion.value = pub
        _mascota.value     = mascotaRepository.findById(pub.idMascota)
        _propietario.value = usuarioRepository.findById(pub.idUsuario)
        _ubicacion.value   = publicacionRepository.findUbicacionById(pub.idUbicacion)

        val fotos = publicacionRepository.fotosByPublicacion(publicacionId)
        _imagenUrl.value = fotos.firstOrNull()?.url
            ?: _mascota.value?.let {
                app.pawpaws.domain.mapper.ImageMapper.resolverImagen(pub.tipoPublicacion, it.tipo)
            } ?: "https://picsum.photos/400/300?random=${publicacionId.hashCode()}"
    }

    fun aprobar() {
        val id = _publicacion.value?.id ?: return
        adminRepository.aprobarPublicacion(id)
        _accionRealizada.value = AccionRevision.Aprobada
    }

    fun rechazar(motivo: String) {
        val id = _publicacion.value?.id ?: return
        adminRepository.rechazarPublicacion(id, motivo)
        _accionRealizada.value = AccionRevision.Rechazada
    }

    fun resetAccion() { _accionRealizada.value = AccionRevision.Ninguna }
}

sealed interface AccionRevision {
    data object Ninguna  : AccionRevision
    data object Aprobada : AccionRevision
    data object Rechazada : AccionRevision
}