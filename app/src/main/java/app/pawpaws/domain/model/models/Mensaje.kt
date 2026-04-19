package app.pawpaws.domain.model.models

data class Mensaje(
    val chatId: String,
    val nombre: String,
    val ultimoMensaje: String,
    val timestamp: Long,
    val leido: Boolean,
    val archivado: Boolean,
    val fotoPerfil: String? = null

)
