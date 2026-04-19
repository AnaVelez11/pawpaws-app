package app.pawpaws.data.repository

import app.pawpaws.domain.model.models.Comentario
import app.pawpaws.domain.repository.ComentarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComentarioRepositoryImpl @Inject constructor() : ComentarioRepository {

    private val _comentarios = MutableStateFlow<List<Comentario>>(emptyList())
    override val comentarios: StateFlow<List<Comentario>> = _comentarios.asStateFlow()

    init {
        _comentarios.value = listOf(
            Comentario("c1", "1", "pub1", "Qué lindo perrito 😍", "2026-04-17"),
            Comentario("c2", "2", "pub1", "Me interesa!", "2026-04-17"),
            Comentario("c3", "3", "pub2", "Disponible?", "2026-04-17")
        )
    }

    override fun agregarComentario(comentario: Comentario) {
        _comentarios.value = _comentarios.value + comentario
    }

    override fun obtenerPorPublicacion(idPublicacion: String): List<Comentario> =
        _comentarios.value.filter { it.idPublicacion == idPublicacion }
}