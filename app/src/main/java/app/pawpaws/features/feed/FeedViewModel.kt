package app.pawpaws.features.feed

import androidx.lifecycle.ViewModel
import app.pawpaws.domain.mapper.ImageMapper
import app.pawpaws.domain.model.enums.TipoPublicacion
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.repository.MascotaRepository
import app.pawpaws.domain.repository.PublicacionRepository
import app.pawpaws.domain.repository.UsuarioRepository
import app.pawpaws.features.publicacion.PublicacionCardData
import app.pawpaws.domain.repository.ResourceProvider
import app.pawpaws.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val publicacionRepository: PublicacionRepository,
    private val mascotaRepository: MascotaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    val publicaciones: StateFlow<List<Publicacion>> = publicacionRepository.publicaciones

    private val _filtroActivo = MutableStateFlow<TipoPublicacion?>(null)
    val filtroActivo: StateFlow<TipoPublicacion?> = _filtroActivo.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    fun setFiltro(tipo: TipoPublicacion?) { _filtroActivo.value = tipo }

    fun onQueryChange(value: String) { _query.value = value }

    fun publicacionesFiltradas(): List<Publicacion> {
        val filtro = _filtroActivo.value
        val texto  = _query.value.trim().lowercase()

        return publicacionRepository.publicaciones.value
            .let { lista -> if (filtro == null) lista else lista.filter { it.tipoPublicacion == filtro } }
            .let { lista ->
                if (texto.isBlank()) lista
                else lista.filter {
                    it.titulo.lowercase().contains(texto) ||
                            it.descripcion.lowercase().contains(texto)
                }
            }
    }

    fun toCardData(publicacion: Publicacion): PublicacionCardData {
        val mascota   = mascotaRepository.findById(publicacion.idMascota)
        val ubicacion = publicacionRepository.findUbicacionById(publicacion.idUbicacion)
        val fotos     = publicacionRepository.fotosByPublicacion(publicacion.id)
        val dueño     = usuarioRepository.findById(publicacion.idUsuario)

        val imagenUrl = fotos.firstOrNull()?.url
            ?: mascota?.let { ImageMapper.resolverImagen(publicacion.tipoPublicacion, it.tipo) }
            ?: ImageMapper.resolverImagen(publicacion.tipoPublicacion, app.pawpaws.domain.model.enums.TipoMascota.PERRO)

        return PublicacionCardData(
            id              = publicacion.id,
            titulo          = publicacion.titulo,
            descripcion     = publicacion.descripcion,
            tipoPublicacion = publicacion.tipoPublicacion,
            estado          = publicacion.estado,
            imagenUrl       = imagenUrl,
            ciudad          = ubicacion?.ciudad ?: resourceProvider.getString(R.string.feed_no_location),
            nombreDueño     = dueño?.nombre ?: resourceProvider.getString(R.string.feed_unknown_user),
            fechaCreacion   = publicacion.fechaCreacion
        )
    }

    fun nombreUsuario(idUsuario: String): String =
        usuarioRepository.findById(idUsuario)?.nombre ?: resourceProvider.getString(R.string.feed_unknown_user)

    fun fotoUsuario(idUsuario: String): String? =
        usuarioRepository.findById(idUsuario)?.fotoPerfil
}