package app.pawpaws.features.publicacion

import androidx.lifecycle.ViewModel
import app.pawpaws.R
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.domain.model.enums.EstadoSolicitud
import app.pawpaws.domain.model.models.Comentario
import app.pawpaws.domain.model.models.FotoPublicacion
import app.pawpaws.domain.model.models.Mascota
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.model.models.Solicitud
import app.pawpaws.domain.model.models.Ubicacion
import app.pawpaws.domain.model.models.Usuario
import app.pawpaws.domain.repository.ComentarioRepository
import app.pawpaws.domain.repository.MascotaRepository
import app.pawpaws.domain.repository.PublicacionRepository
import app.pawpaws.domain.repository.ResourceProvider
import app.pawpaws.domain.repository.SolicitudRepository
import app.pawpaws.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PublicacionDetailViewModel @Inject constructor(
    private val publicacionRepository: PublicacionRepository,
    private val mascotaRepository: MascotaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val solicitudRepository: SolicitudRepository,
    private val comentarioRepository: ComentarioRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _publicacion = MutableStateFlow<Publicacion?>(null)
    val publicacion: StateFlow<Publicacion?> = _publicacion.asStateFlow()

    private val _mascota = MutableStateFlow<Mascota?>(null)
    val mascota: StateFlow<Mascota?> = _mascota.asStateFlow()

    private val _propietario = MutableStateFlow<Usuario?>(null)
    val propietario: StateFlow<Usuario?> = _propietario.asStateFlow()

    private val _ubicacion = MutableStateFlow<Ubicacion?>(null)
    val ubicacion: StateFlow<Ubicacion?> = _ubicacion.asStateFlow()

    private val _fotos = MutableStateFlow<List<FotoPublicacion>>(emptyList())
    val fotos: StateFlow<List<FotoPublicacion>> = _fotos.asStateFlow()

    private val _solicitudResult = MutableStateFlow<RequestResult<Unit>>(RequestResult.Idle)
    val solicitudResult: StateFlow<RequestResult<Unit>> = _solicitudResult.asStateFlow()

    private val _comentarios = MutableStateFlow<List<Comentario>>(emptyList())
    val comentarios: StateFlow<List<Comentario>> = _comentarios.asStateFlow()

    private val _textoComentario = MutableStateFlow("")
    val textoComentario: StateFlow<String> = _textoComentario.asStateFlow()

    fun onTextoComentarioChange(v: String) { _textoComentario.value = v }

    fun cargar(publicacionId: String) {
        val pub = publicacionRepository.findById(publicacionId) ?: return
        _publicacion.value = pub
        _mascota.value = mascotaRepository.findById(pub.idMascota)
        _propietario.value = usuarioRepository.findById(pub.idUsuario)
        _ubicacion.value = publicacionRepository.findUbicacionById(pub.idUbicacion)
        _fotos.value = publicacionRepository.fotosByPublicacion(publicacionId)
        _comentarios.value = comentarioRepository.obtenerPorPublicacion(publicacionId)
    }

    fun enviarSolicitud(idSolicitante: String, mensaje: String) {
        val pub = _publicacion.value ?: return
        val solicitud = Solicitud(
            id = UUID.randomUUID().toString(),
            mensaje = mensaje,
            estado = EstadoSolicitud.PENDIENTE,
            fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            idPublicacion = pub.id,
            idSolicitante = idSolicitante,
            idPropietario = pub.idUsuario
        )
        solicitudRepository.save(solicitud)
        _solicitudResult.value = RequestResult.Success(Unit)
    }

    fun agregarComentario(idUsuario: String) {
        val texto = _textoComentario.value.trim()
        if (texto.isBlank()) return
        val comentario = Comentario(
            id             = UUID.randomUUID().toString(),
            idUsuario      = idUsuario,
            idPublicacion  = _publicacion.value?.id ?: return,
            texto          = texto,
            fecha          = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        )
        comentarioRepository.agregarComentario(comentario)
        _comentarios.value = comentarioRepository.obtenerPorPublicacion(comentario.idPublicacion)
        _textoComentario.value = ""
    }

    fun resetSolicitudResult() { _solicitudResult.value = RequestResult.Idle }
}