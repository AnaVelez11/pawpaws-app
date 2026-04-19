package app.pawpaws.data.model

import app.pawpaws.domain.model.enums.Rol

data class UserSession(
    val userId: String,
    val rol: Rol
)
