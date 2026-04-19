package app.pawpaws.domain.repository

import app.pawpaws.domain.model.models.Comentario
import kotlinx.coroutines.flow.StateFlow

interface ComentarioRepository {

    val comentarios: StateFlow<List<Comentario>>
    fun agregarComentario(comentario: Comentario)
    fun obtenerPorPublicacion(idPublicacion: String): List<Comentario>
}