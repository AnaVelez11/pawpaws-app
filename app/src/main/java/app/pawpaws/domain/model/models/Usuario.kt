package app.pawpaws.domain.model.models

import app.pawpaws.domain.model.enums.EstadoUsuario
import app.pawpaws.domain.model.enums.Rol

data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val password: String,
    val telefono: String? = null,
    val fotoPerfil: String? = null,
    val rol: Rol = Rol.USER,
    val estado: EstadoUsuario = EstadoUsuario.ACTIVO

)
