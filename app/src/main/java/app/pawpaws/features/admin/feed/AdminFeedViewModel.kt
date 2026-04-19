package app.pawpaws.features.admin.feed

import androidx.lifecycle.ViewModel
import app.pawpaws.R
import app.pawpaws.domain.model.enums.TipoMascota
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.repository.AdminRepository
import app.pawpaws.domain.repository.MascotaRepository
import app.pawpaws.domain.repository.PublicacionRepository
import app.pawpaws.domain.repository.ResourceProvider
import app.pawpaws.domain.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AdminFeedViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val usuarioRepository: UsuarioRepository,
    private val mascotaRepository: MascotaRepository,
    private val publicacionRepository: PublicacionRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    val publicaciones: StateFlow<List<Publicacion>> = adminRepository.publicacionesPendientes

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    fun onQueryChange(q: String) { _query.value = q }

    fun filtradas(): List<Publicacion> {
        val texto = _query.value.trim().lowercase()
        return publicaciones.value.let { lista ->
            if (texto.isBlank()) lista
            else lista.filter {
                it.titulo.lowercase().contains(texto) ||
                        it.descripcion.lowercase().contains(texto)
            }
        }
    }

    fun nombreUsuario(id: String): String =
        usuarioRepository.findById(id)?.nombre ?: resourceProvider.getString(R.string.admin_feed_unknown_user)

    fun fotoUsuario(id: String): String? =
        usuarioRepository.findById(id)?.fotoPerfil

    fun resolverImagen(publicacion: Publicacion): String {
        val fotos = publicacionRepository.fotosByPublicacion(publicacion.id)
        if (fotos.isNotEmpty()) return fotos.first().url
        val mascota = mascotaRepository.findById(publicacion.idMascota)
        return mascota?.let {
            app.pawpaws.domain.mapper.ImageMapper.resolverImagen(publicacion.tipoPublicacion, it.tipo)
        } ?: app.pawpaws.domain.mapper.ImageMapper.resolverImagen(publicacion.tipoPublicacion, TipoMascota.PERRO)
    }

    fun ciudad(publicacion: Publicacion): String =
        publicacionRepository.findUbicacionById(publicacion.idUbicacion)?.ciudad ?: resourceProvider.getString(R.string.admin_feed_default_city)
}