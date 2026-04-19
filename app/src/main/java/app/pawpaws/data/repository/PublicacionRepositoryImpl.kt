package app.pawpaws.data.repository

import app.pawpaws.core.utils.resources.ImageResources
import app.pawpaws.domain.model.enums.EstadoPublicacion
import app.pawpaws.domain.model.models.FotoPublicacion
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.model.enums.TipoPublicacion
import app.pawpaws.domain.model.models.Ubicacion
import app.pawpaws.domain.repository.PublicacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicacionRepositoryImpl @Inject constructor() : PublicacionRepository {

    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    override val publicaciones: StateFlow<List<Publicacion>> = _publicaciones.asStateFlow()

    private val _fotos = MutableStateFlow<List<FotoPublicacion>>(emptyList())
    private val _ubicaciones = MutableStateFlow<List<Ubicacion>>(emptyList())

    init {
        _ubicaciones.value = seedUbicaciones()
        _publicaciones.value = seedPublicaciones()
        _fotos.value = seedFotos()
    }

    override fun save(publicacion: Publicacion) { _publicaciones.value += publicacion }

    override fun findById(id: String): Publicacion? =
        _publicaciones.value.firstOrNull { it.id == id }

    override fun findByTipo(tipo: TipoPublicacion): List<Publicacion> =
        _publicaciones.value.filter { it.tipoPublicacion == tipo }

    override fun findByUsuario(idUsuario: String): List<Publicacion> =
        _publicaciones.value.filter { it.idUsuario == idUsuario }

    override fun update(publicacion: Publicacion) {
        _publicaciones.value = _publicaciones.value.map { if (it.id == publicacion.id) publicacion else it }
    }

    override fun delete(id: String) {
        _publicaciones.value = _publicaciones.value.filter { it.id != id }
    }

    override fun saveFoto(foto: FotoPublicacion) { _fotos.value += foto }

    override fun fotosByPublicacion(idPublicacion: String): List<FotoPublicacion> =
        _fotos.value.filter { it.idPublicacion == idPublicacion }

    override fun saveUbicacion(ubicacion: Ubicacion) { _ubicaciones.value += ubicacion }

    override fun findUbicacionById(id: String): Ubicacion? =
        _ubicaciones.value.firstOrNull { it.id == id }

    private fun seedUbicaciones(): List<Ubicacion> = listOf(
        Ubicacion("u1", "Calle 15 #10-20", 4.5339, -75.6811, "Armenia"),
        Ubicacion("u2", "Carrera 13 #5-40", 4.8133, -75.6961, "Pereira"),
        Ubicacion("u3", "Av. Bolívar #20-10", 5.0689, -75.5174, "Manizales")
    )

    private fun seedPublicaciones(): List<Publicacion> = listOf(
        Publicacion(
            id = "p1", titulo = "Max en adopción",
            descripcion = "Labrador de 2 años, muy amigable. Busca hogar amoroso.",
            tipoPublicacion = TipoPublicacion.ADOPCION,
            fechaCreacion = "2025-01-10", estado = EstadoPublicacion.ACTIVA,
            idMascota = "m1", idUbicacion = "u1", idUsuario = "1"
        ),
        Publicacion(
            id = "p2", titulo = "Se perdió Rocky",
            descripcion = "Pastor Alemán, collar rojo. Desapareció en el parque Uribe.",
            tipoPublicacion = TipoPublicacion.PERDIDO,
            fechaCreacion = "2025-01-12", estado = EstadoPublicacion.ACTIVA,
            idMascota = "m3", idUbicacion = "u1", idUsuario = "1"
        ),
        Publicacion(
            id = "p3", titulo = "Luna busca familia",
            descripcion = "Gatita siamés de 1 año. Muy tranquila, ideal para apartamento.",
            tipoPublicacion = TipoPublicacion.ADOPCION,
            fechaCreacion = "2025-01-15", estado = EstadoPublicacion.PENDIENTE_VERIFICACION,
            idMascota = "m2", idUbicacion = "u2", idUsuario = "2"
        ),
        Publicacion(
            id = "p4", titulo = "Encontré un perrito",
            descripcion = "Encontré un perro pequeño color café cerca al centro comercial.",
            tipoPublicacion = TipoPublicacion.ENCONTRADO,
            fechaCreacion = "2025-01-18", estado = EstadoPublicacion.ACTIVA,
            idMascota = "m4", idUbicacion = "u3", idUsuario = "3"
        )
    )

    private fun seedFotos(): List<FotoPublicacion> = listOf(
        FotoPublicacion("f1", ImageResources.PERRO_ADOPCION, "p1"),
        FotoPublicacion("f2", ImageResources.PERRO_PERDIDO, "p2"),
        FotoPublicacion("f3", ImageResources.GATO_ADOPCION, "p3"),
        FotoPublicacion("f4", ImageResources.PERRO_ENCONTRADO, "p4")
    )
}