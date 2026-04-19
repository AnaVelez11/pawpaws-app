package app.pawpaws.domain.repository

import app.pawpaws.domain.model.enums.TipoPublicacion
import app.pawpaws.domain.model.models.FotoPublicacion
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.model.models.Ubicacion
import kotlinx.coroutines.flow.StateFlow


interface PublicacionRepository {

    val publicaciones: StateFlow<List<Publicacion>>
    fun save(publicacion: Publicacion)
    fun findById(id: String): Publicacion?
    fun findByTipo(tipo: TipoPublicacion): List<Publicacion>
    fun findByUsuario(idUsuario: String): List<Publicacion>
    fun update(publicacion: Publicacion)
    fun delete(id: String)

    // Fotos
    fun saveFoto(foto: FotoPublicacion)
    fun fotosByPublicacion(idPublicacion: String): List<FotoPublicacion>

    // Ubicacion
    fun saveUbicacion(ubicacion: Ubicacion)
    fun findUbicacionById(id: String): Ubicacion?
}