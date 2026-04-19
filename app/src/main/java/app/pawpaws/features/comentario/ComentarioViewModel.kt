package app.pawpaws.features.comentario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.pawpaws.R
import app.pawpaws.domain.repository.ResourceProvider
import app.pawpaws.core.utils.extensions.ComentarioUI
import app.pawpaws.domain.model.models.Comentario
import app.pawpaws.domain.repository.ComentarioRepository
import app.pawpaws.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ComentarioViewModel @Inject constructor(
    private val comentarioRepository: ComentarioRepository,
    private val usuarioRepository: UsuarioRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _publicacionId = MutableStateFlow<String?>(null)

    private val _comentarios = MutableStateFlow<List<ComentarioUI>>(emptyList())
    val comentarios: StateFlow<List<ComentarioUI>> = _comentarios.asStateFlow()

    init {
        // Cada vez que cambia cualquier comentario en el repo, re-mapea los de la publicación activa
        comentarioRepository.comentarios
            .onEach { _ ->
                val id = _publicacionId.value ?: return@onEach
                _comentarios.value = mapear(id)
            }
            .launchIn(viewModelScope)
    }

    fun cargarPorPublicacion(idPublicacion: String) {
        _publicacionId.value = idPublicacion
        _comentarios.value = mapear(idPublicacion)
    }

    fun agregarComentario(idUsuario: String, idPublicacion: String, texto: String) {
        if (texto.isBlank()) return
        val comentario = Comentario(
            id            = UUID.randomUUID().toString(),
            idUsuario     = idUsuario,
            idPublicacion = idPublicacion,
            texto         = texto.trim(),
            fecha         = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
        comentarioRepository.agregarComentario(comentario)
    }

    private fun mapear(idPublicacion: String): List<ComentarioUI> {
        val usuarios = usuarioRepository.usuarios.value.associateBy { it.id }

        return comentarioRepository.obtenerPorPublicacion(idPublicacion).map { comentario ->
            val usuario = usuarioRepository.findById(comentario.idUsuario)
            ComentarioUI(
                id = comentario.id,
                nombreUsuario = usuario?.nombre ?: resourceProvider.getString(R.string.comentario_fallback_user),
                fotoUsuario = usuario?.fotoPerfil,
                texto = comentario.texto,
                fecha = comentario.fecha
            )
        }
    }
}