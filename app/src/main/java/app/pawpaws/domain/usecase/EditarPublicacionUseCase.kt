package app.pawpaws.domain.usecase

import app.pawpaws.core.utils.RequestResult
import app.pawpaws.domain.model.enums.EstadoPublicacion
import app.pawpaws.domain.model.enums.TipoMascota
import app.pawpaws.domain.model.enums.TipoPublicacion
import app.pawpaws.domain.repository.MascotaRepository
import app.pawpaws.domain.repository.PublicacionRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class EditarPublicacionUseCase @Inject constructor(
    private val publicacionRepository: PublicacionRepository,
    private val mascotaRepository: MascotaRepository
) {
    fun execute(
        idPublicacion: String,
        titulo: String,
        descripcion: String,
        nombreMascota: String,
        raza: String,
        edad: Int,
        tipoMascota: TipoMascota,
        genero: String,
        tamaño: String,
        vacunasAlDia: Boolean,
        tipoPublicacion: TipoPublicacion
    ): RequestResult<Unit> = try {

        val publicacion = publicacionRepository.findById(idPublicacion)
            ?: return RequestResult.Error("Publicación no encontrada")

        // Actualizar mascota asociada
        val mascotaActual = mascotaRepository.findById(publicacion.idMascota)
        if (mascotaActual != null) {
            mascotaRepository.update(
                mascotaActual.copy(
                    nombre       = nombreMascota.trim(),
                    tipo         = tipoMascota,
                    raza         = raza.trim().ifBlank { "Mestizo" },
                    edad         = edad,
                    genero       = genero,
                    tamaño       = tamaño,
                    descripcion  = descripcion.trim(),
                    vacunasAlDia = vacunasAlDia
                )
            )
        }

        // Actualizar publicación — vuelve a PENDIENTE_VERIFICACION
        publicacionRepository.update(
            publicacion.copy(
                titulo             = titulo.trim(),
                descripcion        = descripcion.trim(),
                tipoPublicacion    = tipoPublicacion,
                estado             = EstadoPublicacion.PENDIENTE_VERIFICACION,
                fechaActualizacion = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
        )

        RequestResult.Success(Unit)

    } catch (e: Exception) {
        RequestResult.Error(e.message ?: "Error al editar publicación")
    }
}