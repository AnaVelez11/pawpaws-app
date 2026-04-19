package app.pawpaws.domain.usecase

import app.pawpaws.core.utils.RequestResult
import app.pawpaws.domain.mapper.ImageMapper
import app.pawpaws.domain.model.enums.EstadoPublicacion
import app.pawpaws.domain.model.enums.TipoMascota
import app.pawpaws.domain.model.enums.TipoPublicacion
import app.pawpaws.domain.model.models.FotoPublicacion
import app.pawpaws.domain.model.models.Mascota
import app.pawpaws.domain.model.models.Publicacion
import app.pawpaws.domain.repository.MascotaRepository
import app.pawpaws.domain.repository.PublicacionRepository
import app.pawpaws.domain.repository.UsuarioRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class CrearPublicacionUseCase @Inject constructor(
    private val publicacionRepository: PublicacionRepository,
    private val mascotaRepository: MascotaRepository,
    private val usuarioRepository: UsuarioRepository
) {
    suspend fun execute(
        titulo: String,
        descripcion: String,
        nombreMascota: String,
        raza: String,
        edad: Int,
        tipoMascota: TipoMascota,
        genero: String,
        tamaño: String,
        vacunasAlDia: Boolean,
        tipoPublicacion: TipoPublicacion,
        idUsuario: String,
        idUbicacion: String,
        imagenes: List<String>
    ): RequestResult<Unit> = try {

        val idMascota     = UUID.randomUUID().toString()
        val idPublicacion = UUID.randomUUID().toString()
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Si no hay imágenes → ImageMapper asigna la correcta según tipo+mascota
        val imagenesFinal = imagenes
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .ifEmpty { listOf(ImageMapper.resolverImagen(tipoPublicacion, tipoMascota)) }

        val mascota = Mascota(
            id           = idMascota,
            nombre       = nombreMascota.trim(),
            tipo         = tipoMascota,
            raza         = raza.trim().ifBlank { "Mestizo" },
            edad         = edad,
            genero       = genero,
            tamaño       = tamaño,
            descripcion  = descripcion.trim(),
            vacunasAlDia = vacunasAlDia,
            idUsuario    = idUsuario
        )
        mascotaRepository.save(mascota)

        val publicacion = Publicacion(
            id              = idPublicacion,
            titulo          = titulo.trim(),
            descripcion     = descripcion.trim(),
            tipoPublicacion = tipoPublicacion,
            fechaCreacion   = fecha,
            estado          = EstadoPublicacion.PENDIENTE_VERIFICACION,
            idMascota       = idMascota,
            idUbicacion     = idUbicacion,
            idUsuario       = idUsuario
        )
        publicacionRepository.save(publicacion)

        // Actualizar usuario (trazabilidad)
        usuarioRepository.findById(idUsuario)?.let { usuarioRepository.update(it) }

        // Guardar foto por defecto
        imagenesFinal.forEach { url ->
            publicacionRepository.saveFoto(
                FotoPublicacion(
                    id            = UUID.randomUUID().toString(),
                    url           = url,
                    idPublicacion = idPublicacion
                )
            )
        }

        RequestResult.Success(Unit)

    } catch (e: Exception) {
        RequestResult.Error(e.message ?: "Error al crear publicación")
    }
}