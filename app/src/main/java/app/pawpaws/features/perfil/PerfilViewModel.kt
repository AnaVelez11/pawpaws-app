package app.pawpaws.features.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.pawpaws.R
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.core.utils.ValidatedField
import app.pawpaws.data.datastore.SessionDataStore
import app.pawpaws.domain.mapper.ImageMapper
import app.pawpaws.domain.model.enums.EstadoSolicitud
import app.pawpaws.domain.model.enums.TipoMascota
import app.pawpaws.domain.model.models.Insignia
import app.pawpaws.domain.model.models.PerfilStats
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.model.models.Solicitud
import app.pawpaws.domain.model.models.Usuario
import app.pawpaws.domain.repository.MascotaRepository
import app.pawpaws.domain.repository.PublicacionRepository
import app.pawpaws.domain.repository.ResourceProvider
import app.pawpaws.domain.repository.SolicitudRepository
import app.pawpaws.domain.repository.UsuarioRepository
import app.pawpaws.domain.usecase.ObtenerPerfilDataUseCase
import app.pawpaws.features.publicacion.PublicacionCardData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val publicacionRepository: PublicacionRepository,
    private val mascotaRepository: MascotaRepository,
    private val solicitudRepository: SolicitudRepository,
    private val sessionDataStore: SessionDataStore,
    private val perfilUseCase: ObtenerPerfilDataUseCase,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _misPublicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    val misPublicaciones: StateFlow<List<Publicacion>> = _misPublicaciones.asStateFlow()

    private val _misSolicitudes = MutableStateFlow<List<Solicitud>>(emptyList())
    val misSolicitudes: StateFlow<List<Solicitud>> = _misSolicitudes.asStateFlow()

    private val _solicitudesRecibidas = MutableStateFlow<List<Solicitud>>(emptyList())
    val solicitudesRecibidas: StateFlow<List<Solicitud>> = _solicitudesRecibidas.asStateFlow()

    private val _stats = MutableStateFlow(PerfilStats(0, 0, 0, 0))
    val stats: StateFlow<PerfilStats> = _stats.asStateFlow()

    private val _insignias = MutableStateFlow<List<Insignia>>(emptyList())
    val insignias: StateFlow<List<Insignia>> = _insignias.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _logoutResult = MutableStateFlow<RequestResult<Unit>>(RequestResult.Idle)
    val logoutResult: StateFlow<RequestResult<Unit>> = _logoutResult.asStateFlow()

    private val _nombreEdit = MutableStateFlow(ValidatedField())
    val nombreEdit: StateFlow<ValidatedField> = _nombreEdit.asStateFlow()

    private val _telefonoEdit = MutableStateFlow(ValidatedField())
    val telefonoEdit: StateFlow<ValidatedField> = _telefonoEdit.asStateFlow()

    private val _publicacionEditando = MutableStateFlow<Publicacion?>(null)
    val publicacionEditando: StateFlow<Publicacion?> = _publicacionEditando.asStateFlow()

    fun cargar(idUsuario: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val u = usuarioRepository.findById(idUsuario)
                _usuario.value = u

                _nombreEdit.value = ValidatedField(u?.nombre ?: "")
                _telefonoEdit.value = ValidatedField(u?.telefono ?: "")

                recargarPublicaciones(idUsuario)
                _misSolicitudes.value = solicitudRepository.findBySolicitante(idUsuario)

                cargarExtras()

            } catch (e: Exception) {
                _errorMessage.value = resourceProvider.getString(R.string.perfil_error_cargar)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun findUsuario(id: String): Usuario? =
        usuarioRepository.findById(id)

    fun findPublicacion(id: String): Publicacion? =
        publicacionRepository.findById(id)

    fun resolverImagenPublicacion(idPublicacion: String): String {
        val fotos = publicacionRepository.fotosByPublicacion(idPublicacion)
        if (fotos.isNotEmpty()) return fotos.first().url
        val pub  = publicacionRepository.findById(idPublicacion) ?: return ""
        val masc = mascotaRepository.findById(pub.idMascota)
        return masc?.let { ImageMapper.resolverImagen(pub.tipoPublicacion, it.tipo) }
            ?: ImageMapper.resolverImagen(pub.tipoPublicacion, TipoMascota.PERRO)
    }

    fun actualizarEstadoSolicitud(idSolicitud: String, nuevoEstado: EstadoSolicitud) {
        solicitudRepository.actualizarEstado(idSolicitud, nuevoEstado)
        // Recargar para reflejar cambios
        val userId = _usuario.value?.id ?: return
        cargar(userId)
    }

    private fun cargarExtras() {
        val (stats, insignias) = perfilUseCase.execute()
        _stats.value = stats
        _insignias.value = insignias
    }

    private fun recargarPublicaciones(idUsuario: String) {
        val publicaciones = publicacionRepository.findByUsuario(idUsuario)
        _misPublicaciones.value = publicaciones

        _solicitudesRecibidas.value = publicaciones.flatMap {
            solicitudRepository.findByPublicacion(it.id)
        }
    }

    fun toCardData(publicacion: Publicacion): PublicacionCardData {
        val mascota   = mascotaRepository.findById(publicacion.idMascota)
        val ubicacion = publicacionRepository.findUbicacionById(publicacion.idUbicacion)
        val fotos     = publicacionRepository.fotosByPublicacion(publicacion.id)
        val dueño     = usuarioRepository.findById(publicacion.idUsuario)

        val imagenUrl = fotos.firstOrNull()?.url
            ?: mascota?.let { ImageMapper.resolverImagen(publicacion.tipoPublicacion, it.tipo) }
            ?: ImageMapper.resolverImagen(
                publicacion.tipoPublicacion,
                TipoMascota.PERRO
            )

        return PublicacionCardData(
            id              = publicacion.id,
            titulo          = publicacion.titulo,
            descripcion     = publicacion.descripcion,
            tipoPublicacion = publicacion.tipoPublicacion,
            estado          = publicacion.estado,
            imagenUrl       = imagenUrl,
            ciudad          = ubicacion?.ciudad ?: resourceProvider.getString(R.string.perfil_ubicacion_default),
            nombreDueño     = dueño?.nombre ?: resourceProvider.getString(R.string.perfil_usuario_default),
            fechaCreacion   = publicacion.fechaCreacion
        )
    }

    fun onNombreChange(v: String) { _nombreEdit.value = ValidatedField(v) }
    fun onTelefonoChange(v: String) { _telefonoEdit.value = ValidatedField(v) }

    fun guardarCambios() {
        val u = _usuario.value ?: return

        if (_nombreEdit.value.value.isBlank()) {
            _nombreEdit.value = ValidatedField("", resourceProvider.getString(R.string.perfil_error_nombre_req))
            return
        }

        val actualizado = u.copy(
            nombre   = _nombreEdit.value.value.trim(),
            telefono = _telefonoEdit.value.value.trim().ifBlank { null }
        )

        usuarioRepository.update(actualizado)
        _usuario.value = actualizado
    }

    fun seleccionarParaEditar(publicacion: Publicacion) {
        _publicacionEditando.value = publicacion
    }

    fun limpiarEdicion() {
        _publicacionEditando.value = null
    }

    fun eliminarPublicacion(idPublicacion: String) {
        viewModelScope.launch {
            try {
                publicacionRepository.delete(idPublicacion)
                val idUsuario = _usuario.value?.id ?: return@launch
                recargarPublicaciones(idUsuario)
            } catch (e: Exception) {
                _errorMessage.value = resourceProvider.getString(R.string.perfil_error_eliminar_pub)
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            sessionDataStore.clearSession()
            _logoutResult.value = RequestResult.Success(Unit)
        }
    }

    fun eliminarCuenta() {
        viewModelScope.launch {
            try {
                val userId = _usuario.value?.id ?: return@launch
                usuarioRepository.delete(userId)
                sessionDataStore.clearSession()
                _logoutResult.value = RequestResult.Success(Unit)
            } catch (e: Exception) {
                _errorMessage.value = resourceProvider.getString(R.string.perfil_error_eliminar_acc)
            }
        }
    }

    fun resetLogout() { _logoutResult.value = RequestResult.Idle }
    fun clearError()  { _errorMessage.value = null }
}
