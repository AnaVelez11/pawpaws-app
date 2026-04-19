package app.pawpaws.domain.mapper

import app.pawpaws.core.utils.resources.ImageResources
import app.pawpaws.domain.model.enums.TipoMascota
import app.pawpaws.domain.model.enums.TipoPublicacion

object ImageMapper {

    fun resolverImagen(
        tipoPublicacion: TipoPublicacion,
        tipoMascota: TipoMascota
    ): String = when (tipoPublicacion) {
        TipoPublicacion.ADOPCION -> when (tipoMascota) {
            TipoMascota.PERRO -> ImageResources.PERRO_ADOPCION
            TipoMascota.GATO  -> ImageResources.GATO_ADOPCION
            TipoMascota.OTRO  -> ImageResources.PERRO_ADOPCION
        }
        TipoPublicacion.PERDIDO -> when (tipoMascota) {
            TipoMascota.PERRO -> ImageResources.PERRO_PERDIDO
            TipoMascota.GATO  -> ImageResources.GATO_ADOPCION
            TipoMascota.OTRO  -> ImageResources.PERRO_PERDIDO
        }
        TipoPublicacion.ENCONTRADO -> when (tipoMascota) {
            TipoMascota.PERRO -> ImageResources.PERRO_ENCONTRADO
            TipoMascota.GATO  -> ImageResources.GATO_ADOPCION
            TipoMascota.OTRO  -> ImageResources.PERRO_ENCONTRADO
        }
    }
}