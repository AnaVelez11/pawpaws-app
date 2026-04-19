package app.pawpaws.domain.model.models

import app.pawpaws.domain.model.enums.TipoMascota

data class Mascota(
    val id: String,
    val nombre: String?  = null,
    val tipo: TipoMascota,
    val raza: String? = null,
    val edad: Int? = null,
    val genero: String,
    val tamaño: String,
    val descripcion: String,
    val vacunasAlDia: Boolean? = null,
    val idUsuario: String
)
